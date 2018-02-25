package jesperl.dk.smoothieaq.server.state;

import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.server.device.classes.*;
import jesperl.dk.smoothieaq.server.task.classes.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.event.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;
import jesperl.dk.smoothieaq.shared.model.task.*;
import jesperl.dk.smoothieaq.util.shared.error.*;
import jesperl.dk.smoothieaq.util.shared.error.Error;
import rx.*;
import rx.Observable;
import rx.Observer;
import rx.schedulers.*;
import rx.subjects.*;

public class  Wires {
	private final static Logger log = Logger.getLogger(Wires.class.getName());
	
	public final Subject<Measure,Measure> devMeasures = new SerializedSubject<>(PublishSubject.create());
	public final Subject<Measure,Measure> devOtherMeasures = new SerializedSubject<>(PublishSubject.create());
	public final Subject<ITask,ITask> tasksScheduled = new SerializedSubject<>(PublishSubject.create());
	public final Subject<TaskDone,TaskDone> tasksDone = new SerializedSubject<>(PublishSubject.create());
	public final Subject<IDevice,IDevice> devicesChanged = new SerializedSubject<>(PublishSubject.create());
	public final Subject<ITask,ITask> tasksChanged = new SerializedSubject<>(PublishSubject.create());
	public final Subject<Error,Error> errors = new SerializedSubject<>(PublishSubject.create());
	public final Subject<Message,Message> messages = new SerializedSubject<>(PublishSubject.create());
	// TODO Message
	
	public final Observable<Long> pulse = Observable.interval(2, 10, TimeUnit.SECONDS, Schedulers.computation()).share();
	
	public final Observable<Event> eventsMux = Observable.merge(
				errors.map(ErrorEvent::create),
				messages.map(MessageEvent::create),
				Observable.merge(devMeasures,devOtherMeasures).map(ME::create),
				devicesChanged.map(DeviceChangeEvent::create),
				tasksChanged.map(TaskChangeEvent::create),
				tasksScheduled.map(TaskScheduledEvent::create)
			)
			.onBackpressureBuffer(1, ()->error(log, 140101, Severity.major, "Wires.eventsMux are being used by someone applying backpressure, that is no good"), BackpressureOverflow.ON_OVERFLOW_DROP_LATEST)
			.share();

	protected final Map<Class<?>, Observer<Object>> savers = new HashMap<>(); 
	protected final Subject<DbClass,DbClass> saveDbClass =  new SerializedSubject<>(PublishSubject.create());

	private State state;
	
	public Subscription devSaveMeasureObserve(IDevice idevice, DeviceStream dstream, Observable<Float> stream) {
		return stream.map(f -> Measure.create(idevice.getId(), dstream, f)).subscribe(devMeasures);
	}
	public Subscription devClientMeasuerObserve(IDevice idevice, DeviceStream dstream, Observable<Float> stream) {
		return stream.map(f -> Measure.create(idevice.getId(), dstream, f)).subscribe(devOtherMeasures);
	}
	
	public Wires(State state) {
		this.state = state;
	}
	
	public void init() {
		pulse.subscribe(); // gets it running hot ...
		eventsMux.subscribe(); // gets it running hot ...
		initSave(Device.class, 50, state.dbContext.dbDevice);
		initSave(DeviceStatus.class, 50, state.dbContext.dbDeviceStatus);
		initSave(DeviceCalibration.class, 50, state.dbContext.dbDeviceCalibration);
		initSave(Task.class, 50, state.dbContext.dbTask);
		initSave(TaskStatus.class, 50, state.dbContext.dbTaskStatus);
		initSave(TaskDone.class, 50, state.dbContext.dbTaskDone);
		devMeasures
//			.map(m -> { m.value = ++state.serno; return m;}) // !!!!!!!!!!!!!! 
			.subscribe(initSave(Measure.class, 200, state.dbContext.dbMeasure));
		saveDbClass.map(Collections::singletonList).subscribe(state.dbContext.dbClass.drain());
//		pulse.map(v -> Measure.create((short)2, DeviceStream.level, 1f)).subscribe(devMeasures);
	}
	
	protected <DBO extends DbObject> Subject<Object,Object> initSave(Class<DBO> cls, int timeout, DbFile<DBO> dbFile) {
		Subject<Object,Object> save = new SerializedSubject<>(PublishSubject.create());
		save.map(cls::cast).buffer(timeout, TimeUnit.MILLISECONDS, 60, Schedulers.io()).subscribe(dbFile.drain());
		savers.put(cls, save);
		return save;
	}
	
	public void save(Object o) { log.fine("saving "+o.getClass().getSimpleName());
		savers.get(o.getClass()).onNext(o);
	}
	
	public void doGuarded(Doit doit) { Errors.doGuarded(e -> { errors.onNext(e.getError()); return e;}, doit); }
	public <T> T funcGuarded(Supplyit<T> doit) { return Errors.funcGuarded(e -> { errors.onNext(e.getError()); return e;}, doit); }
}
