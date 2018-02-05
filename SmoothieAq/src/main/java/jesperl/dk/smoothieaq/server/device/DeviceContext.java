package jesperl.dk.smoothieaq.server.device;

import static jesperl.dk.smoothieaq.util.shared.Objects.*;
import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.util.shared.error.Severity.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

import jesperl.dk.smoothieaq.server.access.abstracts.*;
import jesperl.dk.smoothieaq.server.device.classes.*;
import jesperl.dk.smoothieaq.server.driver.classes.*;
import jesperl.dk.smoothieaq.server.scheduler.Scheduler;
import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.server.task.*;
import jesperl.dk.smoothieaq.server.task.classes.*;
import jesperl.dk.smoothieaq.server.util.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.task.*;
import jesperl.dk.smoothieaq.util.shared.*;
import rx.Observable;

public class  DeviceContext {
	private final static Logger log = Logger.getLogger(DeviceContext.class .getName());

	private State state;
	private DeviceAccessContext daContext;
	private Map<Integer, WDevice<?>> devices = new ConcurrentHashMap<>();
	private Map<Integer, WTask> tasks = new ConcurrentHashMap<>();
	private Map<Integer, Pair<DeviceClass,Driver>> drivers = new ConcurrentHashMap<>();
	private boolean init;

	public DeviceContext(State state, DeviceAccessContext daContext) {
		this.state = state;
		this.daContext = daContext;
		daContext.setSimulate(true); // !!!!!!!!
		init = true;
	}
	
	public void init() {
		loadDrivers();
	}
	
	public void getready() {
		devices.values().stream().filter(d -> !d.isDeleted()).forEach(d -> d.getReady(state));
		init = false;
		scheduleChanged();
	}
	
	public IDevice addDeviceForTest(WDevice<?> idevice) {
		devices.put((int) idevice.getId(), idevice);
		return idevice;
	}
	
	// TODO for test only
	public IDevice load(Device device, DeviceStatus status, DeviceCalibration calibration, List<Task> tasks) {
		return funcGuardedX(() -> {
			WDevice<?> wdevice = createWDevice(device);
			wdevice.init(status);
			wdevice.init(calibration);
			tasks.forEach(t -> wdevice.init(this,t));
			return wdevice;
		}, e -> error(log,e,100103,major,"Could not load device id={0} - {1}",device.id,e.toString()));
	}
	public IDevice load(Device device) {
		return funcGuardedX(() -> {
			WDevice<?> wdevice = createWDevice(device);
//			wdevice.init(status);
//			wdevice.init(calibration);
//			tasks.forEach(t -> wdevice.init(this,t));
			return wdevice;
		}, e -> error(log,e,100103,major,"Could not load device id={0} - {1}",device.id,e.toString()));
	}

	protected WDevice<?> createWDevice(Device device) throws Exception {
		Driver driver = getDriver(device.driverId).getClass().newInstance();
		WDevice<?> wdevice;
		switch(device.deviceClass) {
			case sensor: wdevice = new WSensorDevice(); break;
			case onoff: wdevice = new WOnoffDevice(); break;
			case level: wdevice = new WLevelDevice(); break;
			case toggle: wdevice = new WToggleDevice(); break;
			case doser: wdevice = new WDoserDevice(); break;
			default: throw error(log,100104,major,"Unknown deviceClass {0}",device.deviceClass);
		}
		wdevice.init(state.wires.devicesChanged, this, device, driver);
		devices.put((int) wdevice.getId(), wdevice);
		return wdevice;
	}
	
	public IDevice create(Device device) {
		return funcGuardedX(() -> {
			assert device.id == 0;
			WDevice.validate(state, device);
			state.setNewId(device);
			WDevice<?> wdevice = createWDevice(device);
			state.saveWithId(device);
			wdevice.internalSet(state, DeviceStatusType.disabled);
			wdevice.internalInitialAutoTask(state);
			wdevice.getReady(state);
			return wdevice;
		}, e -> error(log,e,100107,major,"Could not create device id={0} - {1}",device.id,e.toString()));
	}
	
	public IDevice getDevice(int id) { return getWDevice(id); }
	
	public WDevice<?> getWDevice(int id) {
		WDevice<?> wrapper = devices.get(id);
		if (wrapper == null) throw error(log,100101,minor,"No device with id={0}",id);
		return wrapper;
	}
	
	public Observable<IDevice> devices() { return Observable.from(devices.values()); }
	
//	public ITask load(Task task) {
//		
//	}
	
	public void addTask(WTask task) { tasks.put((int) task.getId(), task); }
	
	public WTask getWTask(int id) {
		WTask wrapper = tasks.get(id);
		if (wrapper == null) throw error(log,100106,minor,"No task with id={0}",id);
		return wrapper;
	}
	
	public Observable<ITask> tasks() { return Observable.from(tasks.values()); }
	
	protected void loadDrivers() { loadDrivers("jesperl.dk.smoothieaq.server.driver"); }

	public void loadDriversForTest(String pkg) { loadDrivers(pkg); }

	protected void loadDrivers(String pkg) {
		FindClass.create(pkg)
			.filter(c -> Driver.class.isAssignableFrom(c) && !Modifier.isAbstract(c.getModifiers()))
			.forEach(c -> {
				try {
					addDriver((Driver) c.newInstance());
				} catch (Exception e) {
					log.warning("Could not instantiate "+c.getName()+" "+e.getMessage());
				}
			});
	}

	public Driver addDriverForTest(Driver driver) { return addDriver(driver); }
	
	protected Driver addDriver(Driver driver) {
		int classId = (int) state.getClassId(driver.getClass());
		if (drivers.containsKey(classId)) return null;
		Pair<DeviceClass, Driver> withDeviceClass = withDeviceClass(driver);
		log.info("Loading driver #"+classId+" "+withDeviceClass.a+" "+withDeviceClass.b.getClass().getName());
		drivers.put(classId, withDeviceClass);
		return driver;
	}
	
	private Pair<DeviceClass, Driver> withDeviceClass(Driver driver) {
		if (driver instanceof DoserDriver) return pair(DeviceClass.doser, driver);
		else if (driver instanceof LevelDriver) return pair(DeviceClass.level, driver);
		else if (driver instanceof OnoffDriver) return pair(DeviceClass.onoff, driver);
		else if (driver instanceof SensorDriver) return pair(DeviceClass.sensor, driver);
		else if (driver instanceof ToggleDriver) return pair(DeviceClass.toggle, driver);
		else throw error(log,100105,fatal,"Could not find DeviceClass for Driver {0}",driver.getClass().getName());
	}

	public Driver getDriver(int id) {
		Pair<DeviceClass, Driver> pair = drivers.get(id);
		if (pair == null) throw error(log,100102,minor,"No driver with id={0}",id);
		return pair.b;
	}
	
	public Observable<Triple<Integer,DeviceClass,Driver>> drivers() {
		return Observable.from(drivers.entrySet()).map(e -> triple(e.getKey(), e.getValue().a, e.getValue().b));
	}
	
	public void scheduleChanged() {
		if (init) return;
		Scheduler scheduler = state.scheduler;
		synchronized (scheduler) {
			scheduler.clear();
			tasks.values().forEach(t -> scheduler.addToSchedule(t));
			scheduler.notify();
		}
	}
	
	public State state() { return state; }
	
	public DeviceAccessContext daContext() { return daContext; }
	
	public void release() {
		devices.values().forEach(d -> d.driver().release());
	}
	
}
