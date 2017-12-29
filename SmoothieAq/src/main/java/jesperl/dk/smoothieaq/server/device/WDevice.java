package jesperl.dk.smoothieaq.server.device;

import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.util.shared.error.Severity.*;
import static jesperl.dk.smoothieaq.shared.model.device.DeviceStatusChange.*;
import static jesperl.dk.smoothieaq.shared.model.device.DeviceStatusType.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.util.*;
import java.util.function.*;
import java.util.logging.*;

import jesperl.dk.smoothieaq.server.device.classes.*;
import jesperl.dk.smoothieaq.server.driver.classes.*;
import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.server.task.*;
import jesperl.dk.smoothieaq.server.task.classes.*;
import jesperl.dk.smoothieaq.util.shared.error.Error;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;
import jesperl.dk.smoothieaq.shared.model.task.*;
import jesperl.dk.smoothieaq.util.shared.*;
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
	
	protected PublishSubject<Float> stream = PublishSubject.create();
//	protected Subject<Void,Void> pulse;
	protected Supplier<Observable<Float>> defaultStreamG;
	protected Map<DeviceStream, Pair<MeasurementType,Supplier<Observable<Float>>>> streamsG = new HashMap<>();
	
	private List<Subscription> startSubscriptions = new ArrayList<>();
	
	private Error error;
	
	private Model model = new Model() {
		
		@Override synchronized public IDevice replace(State state, Device device) {
			assert device.getId() == WDevice.this.device.getId();
			assert device.driverId == WDevice.this.device.driverId;
			assert device.deviceClass == WDevice.this.device.deviceClass;
			assert device.deviceType == WDevice.this.device.deviceType;
			validate(state, device);
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
		
		@Override synchronized public ITask add(State state, Task task) {
			WTask.validate(task, WDevice.this);
			assert !tasks.contains(task.id); // OBS!!
			state.save(task);
			WTask wtask = createWTask(state.dContext, task);
			state.wires.tasksChanged.onNext(wtask);
			WDevice.this.scheduleChanged(state);
			return wtask;
		}
		
		@Override public Device getDevice() { return WDevice.this.device; }
		@Override public DeviceStatus getStatus() { return WDevice.this.status; }
		@Override public DeviceCalibration getCalibration() { return WDevice.this.calibration; }
		@Override public List<ITask> getTasks() { return WDevice.this.tasks; }
	};
	
	@Override public Model model() { return model; }

	@SuppressWarnings("unchecked")
	synchronized public void init(DeviceContext context, Device device, Driver driver) {
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
		assert device.driverId != 0 ^ device.deviceCategory == DeviceCategory.manual;
		assert device.deviceUrl != null ^ device.deviceCategory == DeviceCategory.manual;
		assert device.name != null; // should be unique
		assert !EnumSet.of(DeviceClass.sensor, DeviceClass.onoff, DeviceClass.level, DeviceClass.doser, DeviceClass.container, DeviceClass.calculated)
				.contains(device.deviceClass) || device.measurementType != null;
		assert device.deviceClass != DeviceClass.sensor || device.repeatabilityLevel != 0;
		assert device.deviceClass != DeviceClass.onoff || device.onLevel != 0;
	}
	
	synchronized public void init(DeviceCalibration calibration) { this.calibration = calibration; }
	synchronized public void init(DeviceStatus status) { this.status = status; }
	synchronized public void init(DeviceContext context, Task task) { createWTask(context, task); }
	
	protected WTask createWTask(DeviceContext context, Task task) {
		WTask wtask;
		switch(task.taskType) {
			case autoMeasure: wtask = new WAutoMeasureTask(); break;
			case autoOnoff: wtask = new WAutoOnoffTask(); break;
			case autoStatus: wtask = new WAutoStatusTask(); break;
			case autoLevel: wtask = new WAutoLevelTask(); break;
			case autoLevelStream: wtask = new WAutoLevelStreamTask(); break;
			case autoProgram: wtask = new WAutoProgramTask(); break;
			case autoDoseAmount: wtask = new WAutoDoseAmountTask(); break;
			case autoDoseMax: wtask = new WAutoDoseMaxTask(); break;
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

	protected void getReady(State state) { // should only be called from DeviceContext when a device is loaded from disk
		if (isPaused()) { connect(state); stop(state); pause(state); }
		else if (isEnabled()) { enable(state); }
	}
	protected void enable(State state) {
		connect(state);
		stop(state);
		setupStreams(state);
	}
	protected void setupStreams(State state) {}
	protected void connect(State state) {
		doErrorGuarded(() -> { driver().init(state.daContext, device.deviceUrl, funcOrNull(calibration, c -> c.values)); });
	}
	protected void disable(State state) {
		stop(state);
		disconnect();
		teardownStreams();
	}
	protected void teardownStreams() {
		startSubscriptions.forEach(s -> s.unsubscribe());
		startSubscriptions.clear();
	}
	protected void disconnect() {
		driver().release();
	}
	protected void pause(State state) {
		stop(state);
		teardownStreams();
		setupPauseStreams(state);
	}
	protected void setupPauseStreams(State state) {}
	protected void unpause(State state) {
		stop(state);
		teardownStreams();
		setupStreams(state);
	}
	protected void stop(State state) {}
	
	protected void subscription(Subscription s) { startSubscriptions.add(s); }
	protected void subscribeMeasure(State state, DeviceStream ds) { subscription(state.wires.devMeasureObserve(this, ds, stream(ds))); }
	protected void subscribeOtherMeasure(State state, DeviceStream ds) { subscription(state.wires.devOtherMeasuerObserve(this, ds, stream(ds))); }

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
			case delete: disable(state); internalSet(state, deleted); break; // TODO also delete tasks
		}
		return this;
	}
	
	private static final EnumSet<DeviceStatusType> enabledStatus = EnumSet.of(enabled, paused);
	@Override public boolean isEnabled() { return isEnabled(status.statusType); }
	public boolean isEnabled(DeviceStatusType statusType) { return enabledStatus.contains(statusType); }
	public boolean isPaused() { return status.statusType == paused; } 
	
	@Override public Error inError() { return error; }
	@Override public void clearError() { error = null; }
	protected void setError(Error error) { if (this.error == null || this.error.severity.getId() < error.severity.getId()) this.error = error; }
	
	protected void doErrorGuarded(Doit doit) { 
		if (error == null) doGuarded(e -> { setError(error); return null; }, doit);
	}
	
	@Override public Observer<Float> drain() {
		return new Subscriber<Float>() {
			@Override public void onCompleted() { unsubscribe(); }
			@Override public void onError(Throwable e) { unsubscribe(); }
			@Override public void onNext(Float t) { setValue(t); }
		};
	}
//	@Override public Observer<Void> pulse() { return pulse; }
	@Override public Observable<Float> stream() { return defaultStreamG.get(); }
	@Override public Observable<Float> stream(DeviceStream streamId) { return streamsG.get(streamId).b.get(); }
	@Override public Observable<Pair<DeviceStream,MeasurementType>> streams() { return Observable.from(streamsG.entrySet()).map(e -> pair(e.getKey(),e.getValue().a)); }
	protected Observable<Float> baseStream() { return Observable.just(getValue()).concatWith(stream); }
	protected void addDefaultStream(DeviceStream streamId, MeasurementType type, Supplier<Observable<Float>> streamG) { defaultStreamG = streamG; streamsG.put(streamId, pair(type,streamG)); }
	protected void addStream(DeviceStream streamId, MeasurementType type, Supplier<Observable<Float>> streamG) { streamsG.put(streamId, pair(type,streamG)); }

	@Override public String toString() { return device.name+"#"+getId(); }
}
