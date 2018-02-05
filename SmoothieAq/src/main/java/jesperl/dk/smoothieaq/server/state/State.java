package jesperl.dk.smoothieaq.server.state;

import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import jesperl.dk.smoothieaq.server.access.abstracts.*;
import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.server.device.*;
import jesperl.dk.smoothieaq.server.scheduler.*;
import jesperl.dk.smoothieaq.server.scheduler.Scheduler;
import jesperl.dk.smoothieaq.shared.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import rx.Observable;
import rx.functions.*;

// This is a singleton (of some sort)!
public class  State extends SimpleState {
	
	static { state = new State(); }
	static public State state() { return (State) state; }
	
	private AtomicInteger nextId = new AtomicInteger(1); // TODO
	private AtomicInteger nextClassId = new AtomicInteger(1); // TODO

	private Map<Integer,Class<?>> classMap = new ConcurrentHashMap<>();
	private Map<Class<?>,Integer> mapClass = new ConcurrentHashMap<>();
	
	public final NowWithOffset now = new NowWithOffset();
	public final DeviceAccessContext daContext = new DeviceAccessContext(null);
	public final DeviceContext dContext = new DeviceContext(this,daContext);
	public final DbContext dbContext = new DbContext(this);
	public final SchedulerContext sContext = new SchedulerContext(this);
	public final Scheduler scheduler = new Scheduler(sContext);
	public final Thread schedulerThread = new Thread(scheduler);
	public final Wires wires = new Wires(this);

public float serno = 0; // !!!!!!!!!!!!!!
	
	private boolean ready = false;
	
	public void init() {
		dbContext.init();
		wires.init();
		loadClasses();
		schedulerThread.start();
		dContext.init();
		sContext.init();
		load();
		dContext.getready();
		ready = true;
	}
	
	protected void load() {
		// all these should run sequentially, we just do it on the current thread
		loadIdeable(dbContext.dbDevice.stream(), dContext::load);
		loadIdeable(dbContext.dbDeviceStatus.stream(), ds -> dContext.getWDevice(ds.id).init(ds));
		loadIdeable(dbContext.dbTask.stream(), t -> dContext.getWDevice(t.deviceId).init(dContext,t));
		loadIdeable(dbContext.dbTaskStatus.stream(), ts -> dContext.getWTask(ts.id).init(ts));
dbContext.dbMeasure.stream().subscribe(m -> {if (m.value > serno) serno = m.value;}); // !!!!!!!!!!!!!!
	}

	protected <DBO extends DbObject> void loadIdeable(Observable<DBO> stream, Action1<? super DBO> load) {
		IdFirstFilter<DBO> filter = new IdFirstFilter<>();
		stream.filter(filter)
			.doOnTerminate(() -> { if (filter.getMax() >= nextId.get()) nextId.set(filter.getMax()+1); })
			.subscribe(d -> doGuarded(() -> load.call(d), e -> null));
	}

	protected void loadClasses() {
		dbContext.dbClass.stream()
			.doOnNext(dbc -> { if (dbc.id >= nextClassId.get()) nextClassId.set(dbc.id+1); } )
			.subscribe(dbc -> doGuarded(() -> {
				Class<?> cls = this.getClass().getClassLoader().loadClass(dbc.className);
				classMap.put((int)dbc.id, cls);
				mapClass.put(cls, (int)dbc.id);
			}));
	}

	@Override public Date now() { return now.date(); }
	
	public <T extends DbWithId> T save(T object) {
		return saveWithId(setNewId(object));
	}
	
	public <T extends DbWithId> T setNewId(T object) {
		object.setId((short) getNextId());
		return object;
	}

	public <T extends DbWithId> T saveWithId(T object) {
		wires.save(object);
		return object;
	}
	
	public <T extends DbWithId> T replace(T object) {
		wires.save(object);
		return object;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends DbWithStamp> T save(T object) {
		if (object instanceof DbWithId) return (T) save((DbWithId)object);
		wires.save(object);
		return object;
	}
	
	public short getClassId(Class<?> cls) {
		Integer id = mapClass.get(cls);
		if (id == null) {
			id = getNextClassId();
			mapClass.put(cls, id);
			classMap.put(id, cls);
			DbClass dbc = new DbClass();
			dbc.getDate();
			dbc.id = id.shortValue();
			dbc.className = cls.getName();
			wires.saveDbClass.onNext(dbc);
		}
		return id.shortValue();
	}
	
	public Class<?> getClass(int id) { 
		Class<?> cls = classMap.get(id);
		if (cls == null) throw new RuntimeException("Could not find class for id "+id);
		return cls; 
	}
	
	public int getNextId() { return nextId.getAndIncrement(); }

	public int getNextClassId() { return nextClassId.getAndIncrement(); }
	
	public boolean isReady() { return ready; }

}
