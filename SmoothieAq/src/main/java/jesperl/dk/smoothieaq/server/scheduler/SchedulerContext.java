package jesperl.dk.smoothieaq.server.scheduler;

import java.time.*;
import java.util.*;

import jesperl.dk.smoothieaq.server.device.*;
import jesperl.dk.smoothieaq.server.device.classes.*;
import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.server.task.classes.*;
import jesperl.dk.smoothieaq.shared.model.schedule.*;
import jesperl.dk.smoothieaq.shared.model.task.*;

public class  SchedulerContext {
	
	private State state;
	private DeviceContext dContext;

	protected SchedulerContext() {}
	
	public void init() {}
	
	protected int level() { return 1; }

	public SchedulerContext(State state) {
		this.state = state;
		this.dContext = state.dContext;
	}
	
	public SchedulerContext getSchedulerContext() { return this; }

	public State state() { return state; }
	
	public DeviceContext dContext() { return dContext; }

	public Instant instant() { return state().now.instant(); }
	
	public LocalDateTime localDateTime() { return state().now.localDateTime(); }
	
	public ITask getTask(int id) { return dContext().getTask(id); }
	
	public IDevice getDevice(int deviceId) { return dContext().getDevice(deviceId); }
	public List<ITask> getDeviceTasks(int deviceId) { return getDevice(deviceId).model().getTasks(); }
	public ITask getDeviceTask(int deviceId, TaskType type) {
		List<ITask> tasks = getDeviceTasks(deviceId);
		if (tasks == null || tasks.isEmpty()) return null;
		while (type != null) {
			for (ITask task: tasks) if (task.model().getTask().taskType == type) return task;
			type = type.info().parrentType;
		}
		return null;
	}
	
	public Interval last(int taskId) { return dContext.getTask(taskId).last(); }
	
//	public Interval next(Task task) {
//		return null; // TODO
//	}
//	
//	public Interval nextnext(Task task) {
//		return null; // TODO
//	}
//	
//	public Interval prev(Task task) {
//		return null; // TODO
//	}
//	
	public TaskContext taskContext(int taskId) {
		ITask task = getTask(taskId);
		return new TaskContext(getSchedulerContext(),task);
	}
	
//	public Interval next(int taskId) {
//		Pair<? extends Schedule, Interval> next = getTask(taskId).next(taskContext(taskId));
//		if (next == null)
//			return null;
//		return next.b;
//	}
}
