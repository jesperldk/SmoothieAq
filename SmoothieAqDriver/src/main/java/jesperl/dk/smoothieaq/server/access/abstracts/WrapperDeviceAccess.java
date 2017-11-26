package jesperl.dk.smoothieaq.server.access.abstracts;

import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.util.shared.error.Severity.*;

import java.util.logging.*;

import jesperl.dk.smoothieaq.server.access.classes.*;


public abstract class  WrapperDeviceAccess<DA extends DeviceAccess> extends AbstractDeviceAccess {
	private final static Logger log = Logger.getLogger(WrapperDeviceAccess.class .getName());

	private DA da;
	
	@SuppressWarnings("unchecked")
	@Override public DeviceAccess init(DeviceAccessContext context, DeviceUrl url) {
		super.init(context, url);
		doGuardedX(() -> {
			da = (DA) context.get(getWrapperUrl(context,DeviceUrl.create(url.urlString)).urlString);
		}, e -> error(log,e,10020, major, "Malformed deviceAccess url: {0}",url.urlString));
		return this;
	}

	protected abstract DeviceUrl getWrapperUrl(DeviceAccessContext context, DeviceUrl daUrl);
	
	protected DA da() { return da; }
	
	@Override public void store(Class<?> driverCls, Object object) { super.store(driverCls, object); }
	
	@Override public Object retrieve(Class<?> driverCls) { return super.retrieve(driverCls); }
	
	@Override protected void openIt() { da.open(); super.openIt(); }
	
	@Override protected void closeIt() { super.closeIt(); da.release(); }
	
	@Override public void reopen() { da.reopen(); }

	@Override public void flush() { da.flush(); }
	
}
