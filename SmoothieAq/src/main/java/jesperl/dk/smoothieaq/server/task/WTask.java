package jesperl.dk.smoothieaq.server.task;

import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;

import java.util.Date;

import jesperl.dk.smoothieaq.server.device.*;
import jesperl.dk.smoothieaq.server.device.classes.*;
import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.server.streamexpr.*;
import jesperl.dk.smoothieaq.server.streamexpr.node.*;
import jesperl.dk.smoothieaq.server.task.classes.*;
import jesperl.dk.smoothieaq.shared.model.schedule.*;
import jesperl.dk.smoothieaq.shared.model.task.*;

public abstract class  WTask extends IdableType implements ITask {
	
	protected Task task;
	protected TaskStatus status;
	protected WDevice<?> device;

	protected Interval last = null;
	protected Interval next = null;
	protected boolean on = false;
	
	protected StreamApply sApply;

	private Model model = new Model() {
		
		@Override public synchronized ITask replace(State state, Task task) {
			assert task.getId() == WTask.this.task.getId();
			assert task.taskType == WTask.this.task.taskType;
			validate(task,device);
			state.replace(task);
			state.wires.tasksChanged.onNext(WTask.this);
			initInternal(state);
			return WTask.this;
		}

		@Override public Task getTask() { return task; }
		@Override public TaskStatus getStatus() { return status; }
	};
	@Override public Model model() { return model; }
	
	public synchronized WTask init(State state, Task task, WDevice<?> device) {
		assert task.deviceId == device.getId();
		this.task = task;
		this.device = device;
		setId(task.id);
		initInternal(state);
		return this;
	}
	
	protected void initInternal(State state) {
		if (task.whenStream != null) doNoException(() -> sApply = new StreamExprParser().parse(task.whenStream, state.dContext));
		scheduleChanged(state);
	}
	
	public synchronized WTask init(State state, TaskDone done) {
		last = new Interval(new Date(done.start).toInstant(),new Date(done.end).toInstant());
		return this;
	}
	
	public static void validate(Task task, IDevice device) {
		assert task.deviceId == device.getId();
		assert task.taskType.isOfType(TaskType.manual) || task.taskType.info().deviceClass == device.model().getDevice().deviceClass;
		assert task.taskType.info().deviceType == null || task.taskType.info().deviceType == device.model().getDevice().deviceType;
		assert task.whenStream == null || task.taskType.info().whenAllowed;
		assert task.taskType.info().taskArg == null || (task.taskArg != null && task.taskType.info().taskArg.getClass().isAssignableFrom(task.taskArg.getClass()));
		assert task.taskArg == null || task.taskType.info().taskArg != null;
		// TODO validate with proper errors
	}
	
	protected TaskStatus cloneStatus() {
		TaskStatus newStatus = TaskStatus.createS(getId());
		if (status != null) {
			newStatus.statusType = status.statusType;
			newStatus.manualPostponedTo = status.manualPostponedTo;
			newStatus.manualWaitingFrom = status.manualWaitingFrom;
		}
		return newStatus;
	}
	
	protected TaskDone done(State state, boolean manualNotDone, TaskArg arg, String description) {
		TaskDone done = TaskDone.createS(getId());
		done.start = next.a.toEpochMilli();
		done.end = next.b.toEpochMilli();
		done.manualNotDone = manualNotDone;
		done.taskArg = arg;
		done.description = description;
		last = next;
		next = null;
		on = false;
		state.save(done);
		state.wires.tasksDone.onNext(done);
		return done;
	}
	
	protected void internalSet(State state, TaskStatus status) {
		state.save(status);
		this.status = status;
		state.wires.tasksChanged.onNext(this);
		scheduleChanged(state);
	}
	
	protected void scheduleChanged(State state) { device.scheduleChanged(state); }
	protected void notifyScheduled(State state) { state.wires.tasksScheduled.onNext(this); }

	@Override public IDevice getDevice() { return device; }

	@Override public synchronized ITask changeStatus(State state, TaskStatusType statusType) {
		TaskStatus status = cloneStatus();
		status.statusType = statusType;
		internalSet(state, status);
		return this;
	}

	@Override public synchronized boolean isEnabled() { 
		return  device.isEnabled() && 
				status.statusType == TaskStatusType.enabled &&
				status.manualWaitingFrom == 0; 
	}

	@Override public synchronized Interval last() { return last; }
	@Override public synchronized Interval next() { return next; }
	@Override public synchronized boolean on() { return on; }

	@Override public synchronized void scheduled(State state, Interval interval) {
		if (interval == null) {
			if (on) end(state);
		} else {
			next = interval;
			if (task.taskType.info().intervalSchedule && on && !interval.a.isBefore(state.now.instant())) end(state);
		}
		notifyScheduled(state);
	}

	@Override public String toString() { return device+"/Task#"+getId(); }
}
