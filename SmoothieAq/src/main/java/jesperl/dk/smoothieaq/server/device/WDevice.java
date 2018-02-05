package jesperl.dk.smoothieaq.server.device;

import static jesperl.dk.smoothieaq.shared.model.device.DeviceStatusChange.*;
import static jesperl.dk.smoothieaq.shared.model.device.DeviceStatusType.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;
import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.util.shared.error.Severity.*;

import java.util.*;
import java.util.function.*;
import java.util.logging.*;

import jesperl.dk.smoothieaq.server.device.classes.*;
import jesperl.dk.smoothieaq.server.driver.classes.*;
import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.server.task.*;
import jesperl.dk.smoothieaq.server.task.classes.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;
import jesperl.dk.smoothieaq.shared.model.schedule.*;
import jesperl.dk.smoothieaq.shared.model.task.*;
import jesperl.dk.smoothieaq.util.shared.*;
import jesperl.dk.smoothieaq.util.shared.error.Error;
import rx.*;
import rx.Observable;
import rx.Observer;
import rx.subjects.*;

public abstract class  WDevice<DRIVER extends Driver> extends IdableType implements IDevice {
	private final static Logger log = Logger.getLogger(WDevice.class .getName());
	
	protected Device device;
	protected DeviceCalibration calibration;
	protected DeviceStatus status;
	protected List<ITask> tasks = new ArrayList<>();
	private DRIVER driver;
	
	private Subject<IDevice,IDevice> devicesChanged;
	protected PublishSubject<Float> stream = PublishSubject.create();
	protected PublishSubject<Float> pauseStream = PublishSubject.create();
	protected PublishSubject<Float> errorStream = PublishSubject.create();
	protected PublishSubject<Float> alarmStream = PublishSubject.create();
	protected PublishSubject<Float> dueStream = PublishSubject.create();
//	protected Subject<Void,Void> pulse;
//	protected Supplier<Observable<Float>> defaultStreamG;
	protected Map<DeviceStream, Pair<MeasurementType,Supplier<Observable<Float>>>> streamsG = new HashMap<>();
	
	private List<Subscription> startSubscriptions = new ArrayList<>();
	
	private Error error;
	private Set<WTask> dueTasks = new HashSet<>();
	
	private Model model = new Model() {
		
		@Override synchronized public IDevice replace(State state, Device device) {
			assert device.getId() == WDevice.this.device.getId();
			assert device.driverId == WDevice.this.device.driverId;
			assert device.deviceClass == WDevice.this.device.deviceClass;
			assert device.deviceType == WDevice.this.device.deviceType;
			assert device.measurementType == WDevice.this.device.measurementType;
			validate(state, device);
			WDevice.this.device = device;
			init(state.dContext);
			state.replace(device);
			state.wires.devicesChanged.onNext(WDevice.this);
			return WDevice.this;
		}
		
		@Override synchronized public IDevice set(State state, DeviceCalibration calibration) {
			assert calibration.id == 0 || calibration.id == getId();
			// TODO validate
			calibration.id = getId();
			calibration.getDate();
			WDevice.this.calibration = calibration;
			state.save(calibration);
			state.wires.devicesChanged.onNext(WDevice.this);
			return WDevice.this;
		}
//		assert device.id == 0;
//		WDevice.validate(state, device);
//		state.setNewId(device);
//		WDevice<?> wdevice = createWDevice(device);
//		state.saveWithId(device);
//		wdevice.internalSet(state, DeviceStatusType.disabled);
//		return wdevice;
		
		@Override synchronized public ITask add(State state, Task task) {
			assert task.id == 0;
			WTask.validate(task, WDevice.this);
			return addTask(state, task);
		}

		@Override public Device getDevice() { return WDevice.this.device; }
		@Override public DeviceStatus getStatus() { return WDevice.this.status; }
		@Override public DeviceCalibration getCalibration() { return WDevice.this.calibration; }
		@Override public List<ITask> getTasks() { return WDevice.this.tasks; }
	};
	
	@Override public Model model() { return model; }

	@SuppressWarnings("unchecked")
	synchronized public void init(Subject<IDevice,IDevice> devicesChanged, DeviceContext context, Device device, Driver driver) {
		this.devicesChanged = devicesChanged;
		this.setId(device.getId());
		this.driver = (DRIVER) driver;
		this.device = device;
		init(context);
	}

	protected void init(DeviceContext context) {
	}
	
	public static void validate(State state, Device device) {
		assert device.deviceClass != null;
		assert device.deviceType != null;
		assert device.deviceCategory != null;
		assert device.driverId != 0 ^ device.deviceClass == DeviceClass.manual;
		assert device.deviceUrl != null ^ device.deviceClass == DeviceClass.manual;
		assert device.name != null; // should be unique
		assert !EnumSet.of(DeviceClass.sensor, DeviceClass.onoff, DeviceClass.level, DeviceClass.doser, DeviceClass.container, DeviceClass.calculated)
				.contains(device.deviceClass) || device.measurementType != null;
		assert device.deviceClass != DeviceClass.sensor || device.repeatabilityLevel != 0;
		assert device.deviceClass != DeviceClass.onoff || device.onLevel != 0;
	}
	
	synchronized public void init(DeviceCalibration calibration) { this.calibration = calibration; }
	synchronized public void init(DeviceStatus status) { this.status = status; }
	synchronized public void init(DeviceContext context, Task task) { createWTask(context, task); }

	public ITask internalInitialAutoTask(State state) {
		Set<TaskType> types = TaskTypeUtil.autoTypes(device.deviceClass);
		if (types == null || types.isEmpty()) return null;
		TaskType taskType = null;
		for (TaskType type: types) if (!type.info().whenAllowed) taskType = type;
		if (taskType == null) taskType = types.iterator().next();
		Task task = Task.create(getId(), taskType);
		if (taskType.info().whenAllowed) task.whenStream = "0 -> this";
		task.schedule = (taskType.info().intervalSchedule) ? IntervalAllways.create() : PointNever.create();
		return addTask(state, task);
	}
	
	protected ITask addTask(State state, Task task) {
		state.setNewId(task);
		WTask wtask = createWTask(state.dContext, task);
		state.saveWithId(task);
		wtask.changeStatus(state, TaskStatusType.enabled);
		state.wires.tasksChanged.onNext(wtask);
		WDevice.this.scheduleChanged(state);
		return wtask;
	}
			
	protected WTask createWTask(DeviceContext context, Task task) {
		WTask wtask;
		switch(task.taskType) {
//			case autoMeasure: wtask = new WAutoMeasureTask(); break;
			case autoOnoff: case autoOnoffStream: wtask = new WAutoOnoffTask(); break;
			case autoStatusStream: wtask = new WAutoStatusTask(); break;
			case autoLevel: wtask = new WAutoLevelTask(); break;
			case autoLevelStream: wtask = new WAutoLevelStreamTask(); break;
			case autoProgram: wtask = new WAutoProgramTask(); break;
			case autoDoseAmount: wtask = new WAutoDoseAmountTask(); break;
			case autoDoseMax: wtask = new WAutoDoseMaxTask(); break;
			case autoContainerStream: wtask = new WAutoContainerStreamTask(); break;
			case autoCalculatedStream: wtask = new WAutoCalculatedStreamTask(); break;
			default: if (task.taskType.isOfType(TaskType.manual)) wtask = new WManualTask();
					 else throw error(log,100120,major,"Unknown taskType {0}",device.deviceClass);
		}
		wtask.init(context.state(),task,this);
		tasks.add(wtask);
		context.addTask(wtask);
		return wtask;
	}

	protected void internalSet(State state, DeviceStatus status) {
		this.status = status;
		state.save(status);
		state.wires.devicesChanged.onNext(this);
		scheduleChanged(state);
	}
	
	protected void internalSet(State state, DeviceStatusType statusType) {
		DeviceStatus status = DeviceStatus.createS(getId());
		status.statusType = statusType;
		internalSet(state, status);
	}
	
	public void scheduleChanged(State state) {
		state.dContext.scheduleChanged();
	}

	@Override public synchronized boolean isCalibrationNeeded() {
		return false; // TODO
	}
	
	public DRIVER driver() { return driver; }

	/*friend*/ void getReady(State state) { // should only be called from DeviceContext when a device is loaded from disk
		if (isPaused()) { connect(state); stop(state); pause(state); }
		else if (isEnabled()) { enable(state); }
		setupStreams(state);
	}
	protected void enable(State state) {
		connect(state);
		stop(state);
	}
	protected void disable(State state) {
		doDoErrorGuarded(() -> stop(state));
		doDoErrorGuarded(() -> disconnect());
	}
	protected void pause(State state) {
		stop(state);
	}
	protected void unpause(State state) {
		stop(state);
	}
	protected void delete(State state) {
		doDoErrorGuarded(() -> stop(state));
		doDoErrorGuarded(() -> disconnect());
		doDoErrorGuarded(() -> teardownStreams());
	}

	protected void setupStreams(State state) {
		addStream(state, DeviceStream.alarm, MeasurementType.alarm, () -> alarmStream); 
		addStream(state, DeviceStream.error, MeasurementType.onoff, () -> errorStream);
		addStream(state, DeviceStream.duetask, MeasurementType.otherMeasure, () -> dueStream);
	}
	protected void teardownStreams() {
		startSubscriptions.forEach(s -> s.unsubscribe());
		startSubscriptions.clear();
		streamsG.clear();
	}
	protected void connect(State state) {
		doErrorGuarded(() -> { driver().init(state.daContext, device.deviceUrl, funcOrNull(calibration, c -> c.values)); });
	}
	protected void disconnect() {
		driver().release();
		clearError();
	}
	protected void stop(State state) {}
	
	protected void subscription(Subscription s) { startSubscriptions.add(s); }
	protected void subscribeSaveMeasure(State state, DeviceStream ds) { subscription(state.wires.devSaveMeasureObserve(this, ds, stream(ds))); }
	protected void subscribeClientMeasure(State state, DeviceStream ds) { subscription(state.wires.devClientMeasuerObserve(this, ds, stream(ds))); }

	@Override public synchronized DeviceStatusChange[] legalCommands() {
		return (DeviceStatusChange[]) internalLegalCommands().toArray();
	}

	protected EnumSet<DeviceStatusChange> internalLegalCommands() {
		EnumSet<DeviceStatusChange> types = EnumSet.noneOf(DeviceStatusChange.class );
		DeviceStatusType statusType = status.statusType;
		
		if (statusType == deleted) return types;
		types.add(delete);
		if (statusType == disabled) {
			types.add(enable);
		} else {
			types.add(disable);
			if (statusType == DeviceStatusType.enabled)
				types.add(pause);
			else if (statusType == paused)
				types.add(unpause);
		}
		return types;
	}
	
	@Override public synchronized IDevice changeStatus(State state, DeviceStatusChange change) {
		assert internalLegalCommands().contains(change);
		switch(change) {
			case enable: enable(state); internalSet(state, enabled); break;
			case disable: disable(state); internalSet(state, disabled); break;
			case unpause: unpause(state); internalSet(state, enabled); break;
			case pause: pause(state); internalSet(state, paused); break;
			case delete: delete(state); internalSet(state, deleted); break; // TODO also delete tasks
		}
		return this;
	}
	
	private static final EnumSet<DeviceStatusType> enabledStatus = EnumSet.of(enabled, paused);
	@Override public boolean isEnabled() { return isEnabled(status.statusType); }
	public boolean isEnabled(DeviceStatusType statusType) { return enabledStatus.contains(statusType); }
	public boolean isPaused() { return status.statusType == paused; } 
	public boolean isDeleted() { return status.statusType == deleted; } 
	
	@Override public Float dueTasks() { return (float)dueTasks.size(); }
	public void setDue(WTask task, boolean due) {
		if (due && !dueTasks.contains(task)) {
			dueTasks.add(task);
			dueStream.onNext((float)dueTasks.size());
		} else if (!due && dueTasks.contains(task)) {
			dueTasks.remove(task);
			dueStream.onNext((float)dueTasks.size());
		}
	}
	
	@Override public Float alarm() { return 0f; }
	
	@Override public Error inError() { return error; }
	@Override public void clearError() { 
		if (error != null) { 
			error = null; 
			errorStream.onNext(0f); 
			devicesChanged.onNext(this); 
		} 
	}
	protected void setError(Error error) { 
		if (this.error == null || this.error.severity.getId() < error.severity.getId()) {
			this.error = error; 
			errorStream.onNext(1f); 
			devicesChanged.onNext(this); 
		}
	}
	
	public void doErrorGuarded(Doit doit) { if (error == null) doGuarded(e -> { setError(e.getError()); return null; }, doit); }
	public void doDoErrorGuarded(Doit doit) { doGuarded(e -> { setError(e.getError()); return null; }, doit); }
	
	@Override public Observer<Float> drain() {
		return new Subscriber<Float>() {
			@Override public void onCompleted() { unsubscribe(); }
			@Override public void onError(Throwable e) { unsubscribe(); }
			@Override public void onNext(Float t) { setValue(t); }
		};
	}
//	@Override public Observer<Void> pulse() { return pulse; }
	@Override public Observable<Float> stream() { return stream(DeviceUtil.toDefaultStream.get(device.deviceClass)); }
	@Override public Observable<Float> stream(DeviceStream streamId) { return streamsG.get(streamId).b.get(); }
	@Override public Observable<Pair<DeviceStream,MeasurementType>> streams() { return Observable.from(streamsG.entrySet()).map(e -> pair(e.getKey(),e.getValue().a)); }
	protected Observable<Float> baseStream() { return Observable.just(getValue()).concatWith(stream); }
	
	protected void addStream(State state, DeviceStream streamId, MeasurementType type, Supplier<Observable<Float>> streamG) {
		DeviceStream typeStream = streamId == DeviceStream.pauseX ? DeviceUtil.toPauseShadowStream.get(device.deviceClass) : streamId;
		if (DeviceStreamUtil.toType.get(typeStream) == DeviceStreamType.continousStream) {
			Observable<Float> stream = streamG.get().replay(1).autoConnect();
			subscription( stream.subscribe() ); // gets it running hot...
			streamG = () -> stream;
		}
		streamsG.put(streamId, pair(type,streamG)); 
		if (DeviceUtil.toSaveStreams.get(device.deviceClass).contains(streamId) || (streamId == DeviceStream.level && device.dontSaveLevel)) 
			subscribeSaveMeasure(state, streamId);
		else if (DeviceUtil.toClientStreams.get(device.deviceClass).contains(streamId) || (streamId == DeviceStream.level && device.dontClientLevel)) 
			subscribeClientMeasure(state, streamId);
	}

	@Override public String toString() { return device.name+"#"+getId(); }
}
