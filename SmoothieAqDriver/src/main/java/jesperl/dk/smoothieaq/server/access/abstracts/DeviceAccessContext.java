package jesperl.dk.smoothieaq.server.access.abstracts;

import static jesperl.dk.smoothieaq.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.shared.error.Severity.*;
import static jesperl.dk.smoothieaq.shared.util.Objects.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;

import jesperl.dk.smoothieaq.server.access.classes.*;
import jesperl.dk.smoothieaq.shared.error.*;
import jesperl.dk.smoothieaq.shared.error.Error;
import jesperl.dk.smoothieaq.shared.util.*;

public class DeviceAccessContext {
	private final static Logger log = Logger.getLogger(DeviceAccessContext.class.getName());
	
	private static class CachedEnumeration {
		public long stamp;
		public List<Pair<DeviceUrl,String>> enumeration;
	}
	
	private Consumer<Pair<String, Error>> errorListener;
	private Map<String,Supplier<AbstractDeviceAccess>> instantiaters = new HashMap<>();
	private Map<String,Supplier<List<Pair<DeviceUrl,String>>>> enumerators = new HashMap<>();
	private Map<String, AbstractDeviceAccess> deviceAccesses = new HashMap<>();
	public Map<String,CachedEnumeration> cachedEnumerations = new HashMap<>();
	private boolean simulate = false;
	
	public DeviceAccessContext(Consumer<Pair<String, Error>> errorListener) { 
		this.errorListener = errorListener; 
	}
	
	@SuppressWarnings("unchecked")
	synchronized public <T extends DeviceAccess> T get(Class<T> cls, String urlString) { // bus://busSelector:arg1.argN/deviceSelector:arg1.argN
		return (T)get(urlString);
	}
	
	synchronized public DeviceAccess get(String urlString) { // bus://busSelector:arg1.argN/deviceSelector:arg1.argN
		DeviceUrl url = DeviceUrl.create(urlString);
		return nnv(deviceAccesses.get(url.deviceKey()), () -> create(url)).init(this, url);
	}
	
	synchronized public void release(DeviceAccess da) { deviceAccesses.remove(da.getUrl().deviceKey()); }

	protected AbstractDeviceAccess create(DeviceUrl url) { 
		AbstractDeviceAccess da = getInstantiater(url).get(); 
		deviceAccesses.put(url.deviceKey(), da); 
		return da; 
	}
	
	protected Supplier<AbstractDeviceAccess> getInstantiater(DeviceUrl url) {
		Supplier<AbstractDeviceAccess> instantiater = instantiaters.get(url.bus);
		if (instantiater == null) {
			try {
				final Class<AbstractDeviceAccess> cls = getDeviceAccessClass(url.bus);
				instantiater = () -> funcGuardedX(() -> cls.newInstance(), e ->  error(log,e,10003,major,"Can not handle buss >{0}<",url.bus));
			} catch (Exception e) {
				log.warning("Could not get DeviceAccess class for bus "+url.bus);
				instantiater = () -> { throw error(log,10002,major,"Can not handle buss >{0}<",url.bus); };
			}
			instantiaters.put(url.bus, instantiater);
		}
		return instantiater;
	}

	@SuppressWarnings("unchecked")
	protected Class<AbstractDeviceAccess> getDeviceAccessClass(String bus) throws ClassNotFoundException {
		String clsName = "jesperl.dk.smoothieaq.server.access."+bus.substring(0, 1).toUpperCase()+bus.substring(1)+DeviceAccess.class.getSimpleName();
		Class<?> cls = this.getClass().getClassLoader().loadClass(clsName);
		return (Class<AbstractDeviceAccess>) cls;
	}
	
	synchronized public List<Pair<DeviceUrl,String>> enumerate(String bus) {
		synchronized (this) {
			CachedEnumeration ce = cachedEnumerations.get(bus);
			if (ce != null && ce.stamp + 10000 > System.currentTimeMillis()) return ce.enumeration;
		}
		CachedEnumeration ce = new CachedEnumeration();
		ce.stamp = System.currentTimeMillis();
		ce.enumeration = getEnumerator(bus).get();
		synchronized (this) { cachedEnumerations.put(bus, ce); }
		return ce.enumeration;
	}
	
	@SuppressWarnings("unchecked")
	protected Supplier<List<Pair<DeviceUrl,String>>> getEnumerator(String bus) {
		Supplier<List<Pair<DeviceUrl,String>>> enumerator = enumerators.get(bus);
		if (enumerator == null) {
			try {
				Method method = getDeviceAccessClass(bus).getMethod("enumerate", this.getClass());
				enumerator = () -> funcNoException(() -> (List<Pair<DeviceUrl,String>>)method.invoke(null,this), Collections.emptyList());
			} catch (Exception e) {
				enumerator = Collections::emptyList;
			}
			enumerators.put(bus, enumerator);
		}
		return enumerator;
	}
	
	synchronized public void setSimulate(boolean simulate) { this.simulate = simulate; }
	public boolean isSimulate() { return simulate; }
	
	public Consumer<Pair<String, Error>> getErrorListener() { return errorListener; }
	
	synchronized public void release() {
		ArrayList<DeviceAccess> das = new ArrayList<>(deviceAccesses.values());
		for (DeviceAccess da: das) doNoException(() -> da.forceRelease());
	} 
	
	public ErrorException er(DeviceAccess da, ErrorException ee) { if (errorListener != null) errorListener.accept(pair(da.getUrl().deviceKey(),ee.getError())); return ee; }
}
