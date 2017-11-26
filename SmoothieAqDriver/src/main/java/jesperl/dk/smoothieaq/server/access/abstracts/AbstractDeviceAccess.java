package jesperl.dk.smoothieaq.server.access.abstracts;

import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.util.shared.error.Severity.*;

import java.util.*;
import java.util.function.*;
import java.util.logging.*;

import jesperl.dk.smoothieaq.server.access.classes.*;
import jesperl.dk.smoothieaq.util.shared.error.*;

public abstract class  AbstractDeviceAccess implements DeviceAccess {
	private final static Logger log = Logger.getLogger(AbstractDeviceAccess.class .getName());
	
	public static String bus(Class<? extends DeviceAccess> cls) { return cls.getSimpleName().substring(0, cls.getSimpleName().length()-DeviceAccess.class .getSimpleName().length()).toLowerCase(); }
	
	private DeviceAccessContext context;
	private int count = 0;
	private DeviceUrl url;
	private boolean deviceOpen = false;
	private Map<Class<?>,Object> driverStorage = new HashMap<>();
	
	protected final ErrorHandler eh = new ErrorHandler() {
		@Override public ErrorException apply(ErrorException ee) { context.er(AbstractDeviceAccess.this, ee); return ee; }
	};
	
	public DeviceAccess init(DeviceAccessContext context, DeviceUrl url) {
		if (count == 0)	this.url = url;
		else if (!url.urlString.equals(this.url.urlString)) throw error(log,10010,major,"Device can not be opened with different URL's >{0}< and '{1}",url.urlString,this.url.urlString);
		count++;
		this.context = context;
		return this;
	}
	
	@Override synchronized public void store(Class<?> driverCls, Object object) { driverStorage.put(driverCls, object); }
	@Override synchronized public Object retrieve(Class<?> driverCls) { return driverStorage.get(driverCls); }
	synchronized public <T> T retrieveOrCreate(Class<?> driverCls, Supplier<T> newStorage) {
		@SuppressWarnings("unchecked") T t = (T) driverStorage.get(driverCls);
		if (t == null) driverStorage.put(driverCls, t = newStorage.get());
		return t;
	}

	@Override synchronized public boolean isOpen() { return deviceOpen; }
	
	@Override synchronized public void open() {
		if (count == -1) throw error(log,10004,fatal,"Trying to open released DeviceAccess '{0}",url.urlString);
		if (!isOpen()) openIt(); 
	}
	
	protected void openIt() { deviceOpen = true; }
	
	@Override synchronized public void release() { if (--count == 0) forceRelease(); }
	@Override synchronized public void forceRelease() { count = -1; if (deviceOpen) closeIt(); context.release(this); }
	
	protected void closeIt() { deviceOpen = false; }
	
	@Override synchronized public void reopen() {
		if (isOpen()) { doNoException(() -> closeIt()); deviceOpen = false; }
		openIt();
	}
	
	@Override synchronized public Boolean isPressent() { return null; } // null -> we can not test it
	
	@Override synchronized public void flush() {} // Subclasses must override!

//	synchronized public byte[] writeThenRead(byte[] request, int start, int length, int readLength, Byte until) { return null; }  // Subclasses must override!
//	
//	public static final Function<byte[],byte[]> nullMap = new Function<byte[], byte[]>() {
//		@Override public byte[] apply(byte[] t) { return t; }
//	};
//
//	public static final Function<byte[],String> strMap = new Function<byte[], String>() {
//		@Override public String apply(byte[] t) { return new String(t); }
//	};
//
//	public <T> T writeThenRead(String request, int readLength, Byte until, int retries, int millisBetween, Function<byte[], T> mapAndValidate) {
//		if (request == null)
//			return writeThenRead(null, 0, 0, readLength, until, retries, millisBetween, mapAndValidate);
//		else
//			return writeThenRead(request.getBytes(), 0, request.length(), readLength, until, retries, millisBetween, mapAndValidate);
//	}
//
//	public <T> T writeThenRead(byte[] request, int readLength, Byte until, int retries, int millisBetween, Function<byte[], T> mapAndValidate) {
//		if (request == null)
//			return writeThenRead(null, 0, 0, readLength, until, retries, millisBetween, mapAndValidate);
//		else
//			return writeThenRead(request, 0, request.length, readLength, until, retries, millisBetween, mapAndValidate);
//	}
//
//	synchronized public <T> T writeThenRead(byte[] request, int start, int length, int readLength, Byte until, int retries, int millisBetween, Function<byte[], T> mapAndValidate) {
//		return retry(retries,millisBetween,() -> mapAndValidate.apply(writeThenRead(request, start, length, readLength, until)),null); 
//	}
	
	@Override
	public DeviceUrl getUrl() { return url; }

	@Override protected void finalize() throws Throwable {
		forceRelease();
		super.finalize();
	}
	
}
