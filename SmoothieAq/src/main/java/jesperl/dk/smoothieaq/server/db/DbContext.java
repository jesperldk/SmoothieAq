package jesperl.dk.smoothieaq.server.db;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;
import jesperl.dk.smoothieaq.shared.model.task.*;

public class  DbContext {
	private final static Logger log = Logger.getLogger(DbContext.class .getName());
	
	private State state;
	private String dbRoot = ".";
	
	private Map<Class<?>,Map<Integer,Enum<?>>> enums = new ConcurrentHashMap<>();
	
	public DbFile<Device> dbDevice;
	public DbFile<DeviceStatus> dbDeviceStatus;
	public DbFile<DeviceCalibration> dbDeviceCalibration;
	public DbFile<Task> dbTask;
	public DbFile<TaskStatus> dbTaskStatus;
	public DbFile<TaskDone> dbTaskDone;
	public DbFile<Measure> dbMeasure;
	public DbFile<DbClass> dbClass;
	
	public DbContext(State state) { 
		this.state = state;
	}
	
	public void init() {
		String root = System.getProperty("smoothieAq.db.dir");
		if (root != null && !root.isEmpty()) dbRoot = root;
		log.info("dbRoot="+dbRoot);
		
		dbDevice = DbFile.create(Device.class , false, this);
		dbDeviceStatus = DbFile.create(DeviceStatus.class , false, this);
		dbDeviceCalibration = DbFile.create(DeviceCalibration.class , false, this);
		dbTask = DbFile.create(Task.class , false, this);
		dbTaskStatus = DbFile.create(TaskStatus.class , false, this);
		dbTaskDone = DbFile.create(TaskDone.class , false, this);
		dbMeasure = DbFile.create(Measure.class , true, this);
		dbClass = DbFile.create(DbClass.class , false, this);
	}
	
	@SuppressWarnings("unchecked")
	public <E extends Enum<?>> E getEnum(Class<E> cls, int id) {
		if (id == 0) return null;
		try {
			Map<Integer, Enum<?>> map = enums.get(cls);
			if (map == null) {
				map = new ConcurrentHashMap<>();
				Method getId = cls.getMethod("getId");
				for (E e: cls.getEnumConstants()) {
					map.put((Integer) getId.invoke(e), e);
				}
				enums.put(cls, map);
			}
			return (E) map.get(id);
		} catch (Exception e) { throw new RuntimeException(e); }
	}
	
	public String getDbRoot() { return dbRoot; }
	
	public State state() { return state; }
}
