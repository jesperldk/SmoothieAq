package jesperl.dk.smoothieaq.server.scheduler;

import java.time.*;

import jesperl.dk.smoothieaq.server.device.*;
import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.server.task.classes.*;
import jesperl.dk.smoothieaq.shared.model.schedule.*;

public class  TaskContext extends SchedulerContext {
	
	private SchedulerContext parrent;
	private ITask task;
	protected int level;

	protected TaskContext(TaskContext parrent) {
		this(parrent, parrent.task);
	}
	
	public TaskContext(SchedulerContext parrent, ITask task) {
		this.parrent = parrent;
		this.task = task;
		level = parrent.level()+1;
		assert level < 8;
	}
	
	@Override protected int level() { return level; }
	
	@Override public SchedulerContext getSchedulerContext() { return parrent.getSchedulerContext(); }

	public void setLast(Interval intv) { }
	
	public Instant created() { return task.model().getTask().getInstant(); }
	
	public Interval last() { return last(task.getId()); }
	
	public SchedulerContext getParrent() { return parrent; }

	public ITask getTask() { return task; }

	@Override public State state() { return parrent.state(); }
	
	@Override public DeviceContext dContext() { return parrent.dContext(); }
	
	public ITask getDeviceTask(int deviceId) { return getDeviceTask(deviceId, task.model().getTask().taskType); }

	@Override public Instant instant() { return parrent.instant(); }

	@Override public LocalDateTime localDateTime() { return parrent.localDateTime(); }
	
	@Override public Interval last(int taskId) { return parrent.last(taskId); }

	public OffsetTaskContext offsetTo(Instant offsetTo) { return new OffsetTaskContext(this, offsetTo); }
	
	public OffsetTaskContext offsetTo(Interval next) { return new OffsetTaskContext(this, next); }
	
	@Override public void init() { throw new RuntimeException("don't"); }
}
