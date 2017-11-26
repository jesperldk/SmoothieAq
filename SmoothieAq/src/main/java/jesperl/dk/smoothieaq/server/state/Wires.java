package jesperl.dk.smoothieaq.server.state;

import java.util.*;
import java.util.concurrent.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.server.device.classes.*;
import jesperl.dk.smoothieaq.server.task.classes.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;
import jesperl.dk.smoothieaq.shared.model.task.*;
import rx.*;
import rx.Observable;
import rx.Observer;
import rx.schedulers.*;
import rx.subjects.*;

public class Wires {
	
	public final Subject<Measure,Measure> devMeasure = new SerializedSubject<>(PublishSubject.create());
	public final Subject<Measure,Measure> devOnoffX = new SerializedSubject<>(PublishSubject.create());
	public final Subject<ITask,ITask> taskScheduled = new SerializedSubject<>(PublishSubject.create());
	public final Subject<TaskDone,TaskDone> taskDone = new SerializedSubject<>(PublishSubject.create());
	public final Subject<IDevice,IDevice> deviceChanged = new SerializedSubject<>(PublishSubject.create());
	public final Subject<ITask,ITask> taskChanged = new SerializedSubject<>(PublishSubject.create());
	// TODO Message
	
	public final Observable<Long> pulse = Observable.interval(2, 20, TimeUnit.SECONDS, Schedulers.computation()).share();

	protected final Map<Class<?>, Observer<Object>> savers = new HashMap<>(); 
	protected final Subject<DbClass,DbClass> saveDbClass =  new SerializedSubject<>(PublishSubject.create());

	private State state;
	
	public Subscription devMeasureObserve(IDevice idevice, DeviceStream dstream, Observable<Float> stream) {
		return stream.map(f -> Measure.create(idevice.getId(), dstream, f)).subscribe(devMeasure);
	}
	public Subscription devOnoffxObserve(IDevice idevice, DeviceStream dstream, Observable<Float> stream) {
		return stream.map(f -> Measure.create(idevice.getId(), dstream, f)).subscribe(devMeasure);
	}
	
	public Wires(State state) {
		this.state = state;
	}
	
	public void init() {
		initSave(Device.class, 50, state.dbContext.dbDevice);
		initSave(DeviceStatus.class, 50, state.dbContext.dbDeviceStatus);
		initSave(DeviceCalibration.class, 50, state.dbContext.dbDeviceCalibration);
		initSave(Task.class, 50, state.dbContext.dbTask);
		initSave(TaskStatus.class, 50, state.dbContext.dbTaskStatus);
		initSave(TaskDone.class, 50, state.dbContext.dbTaskDone);
		devMeasure.subscribe(initSave(Measure.class, 200, state.dbContext.dbMeasure));
		saveDbClass.map(Collections::singletonList).subscribe(state.dbContext.dbClass.drain());
	}
	
	protected <DBO extends DbObject> Subject<Object,Object> initSave(Class<DBO> cls, int timeout, DbFile<DBO> dbFile) {
		Subject<Object,Object> save = new SerializedSubject<>(PublishSubject.create());
		save.map(cls::cast).buffer(timeout, TimeUnit.MILLISECONDS, 30, Schedulers.io()).subscribe(dbFile.drain());
		savers.put(cls, save);
		return save;
	}
	
	public void save(Object o) {
		savers.get(o.getClass()).onNext(o);
	}
}
