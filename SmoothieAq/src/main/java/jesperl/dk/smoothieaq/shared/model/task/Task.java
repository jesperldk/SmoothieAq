package jesperl.dk.smoothieaq.shared.model.task;

import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.nio.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.server.scheduler.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jesperl.dk.smoothieaq.shared.model.schedule.*;
import jesperl.dk.smoothieaq.util.shared.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  Task extends DbWithId implements Task_Helper {

	public int deviceId;
	public TaskType taskType; 
	public TaskArg taskArg;
	public Schedule schedule;
	public String whenStream;

	@JsOverlay 
	public static Task create(int deviceId, TaskType taskType) {
		return create(deviceId,taskType,cloneArg(taskType),null);
	}
	
	@JsOverlay 
	public static Task create(int deviceId, TaskType taskType, TaskArg taskArg, Schedule schedule) {
		Task task = new Task();
		task.deviceId = (short) deviceId;
		task.taskType = taskType;
		task.taskArg = taskArg;
		task.schedule = schedule;
		return task;
	}

	@JsOverlay 
	static private TaskArg cloneArg(TaskType taskType) {
		if (taskType == null) return null;
		TaskArg taskArg = TaskTypeUtil.info(taskType).taskArg;
		if (taskArg == null) return null;
		return taskArg.copy();
	}

	@GwtIncompatible final public Pair<? extends Schedule,Interval> next(SchedulerContext context) {
		return nextForTask(context.taskContext(getId()));
	}

	@GwtIncompatible public Pair<? extends Schedule, Interval> nextForTask(TaskContext taskContext) {
		return pair(schedule, schedule.nextInterval(taskContext));
	}

//	@GwtIncompatible
//	public Pair<? extends Schedule, Interval> nextForTask(TaskContext context) {
//		return Arrays.stream(schedules)
//				.map(s -> new Pair<Schedule, Interval>(s,s.nextInterval(context)))
//				.min( (p1,p2) -> p1.b.start().compareTo(p2.b.start()) ).orElse(null);
//	}

	@JsOverlay public final Task copy() { return Task_Db.copy(new Task(),this); }
	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { Task_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public Task deserialize(int ver, ByteBuffer in, DbContext context) { return Task_Db.deserializeFields(this, in, context); }
}
