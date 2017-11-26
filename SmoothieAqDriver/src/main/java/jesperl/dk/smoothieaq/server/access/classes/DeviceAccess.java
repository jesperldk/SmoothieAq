package jesperl.dk.smoothieaq.server.access.classes;

public interface DeviceAccess {

	void store(Class<?> driverCls, Object object);

	Object retrieve(Class<?> driverCls);

	boolean isOpen();

	void open();

	void reopen();

	Boolean isPressent(); // null -> we can not test it

	void flush();

	void release();
	
	void forceRelease();
	
	DeviceUrl getUrl();

}