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

//	private ConcurrentHashMap<Integer,Idable> objects = new ConcurrentHashMap<>();

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
	
	private boolean ready = false;
	
	public void init() {
		dbContext.init();
		schedulerThread.start();
		dContext.init();
		sContext.init();
		load();
		dContext.getready();
		wires.init();
		ready = true;
	}
	
	protected void load() {
		loadClasses();
		loadIdeable(dbContext.dbDevice.stream(), dContext::load);
		loadIdeable(dbContext.dbDeviceStatus.stream(), ds -> dContext.getWDevice(ds.id).init(ds));
		
		// TODO Auto-generated method stub
		
	}

	protected <DBO extends DbObject> void loadIdeable(Observable<DBO> stream, Action1<? super DBO> load) {
		IdFirstFilter<DBO> filter = new IdFirstFilter<>();
		stream.filter(filter).subscribe(load);
		if (filter.getMax() > nextId.get()) nextId.set(filter.getMax()+1);
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
	
//	@SuppressWarnings("unchecked")
//	public <T extends DbWithId> T get(Class<T> cls, int id) {
//		return (T) objects.get(id);
//	}
	
	public <T extends DbWithId> T save(T object) {
		object.setId((short) getNextId());
//		objects.put((int) object.getId(), object);
		wires.save(object);
		return object;
	}
	
	public <T extends DbWithId> T replace(T object) {
//		assert objects.get(object.getId()) != null;
//		objects.put((int) object.getId(), object);
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
	
	public Class<?> getClass(int id) { return classMap.get(id); }
	
	public int getNextId() { return nextId.getAndIncrement(); }

	public int getNextClassId() { return nextClassId.getAndIncrement(); }
	
	public boolean isReady() { return ready; }

}
