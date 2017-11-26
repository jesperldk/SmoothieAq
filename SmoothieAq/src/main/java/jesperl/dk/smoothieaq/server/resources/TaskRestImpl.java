package jesperl.dk.smoothieaq.server.resources;

import java.util.*;
import java.util.function.*;

import jesperl.dk.smoothieaq.server.streamexpr.*;
import jesperl.dk.smoothieaq.server.task.classes.*;
import jesperl.dk.smoothieaq.shared.model.task.*;
import jesperl.dk.smoothieaq.shared.resources.*;
import rx.*;
import rx.Observable;

public class TaskRestImpl extends RestImpl implements TaskRest {

	protected ITask itask(int taskId) { return context().getTask(taskId); }

	@Override public Single<Task> get(int taskId) {
		return Single.just(itask(taskId).model().getTask());
	}

	@Override public Single<TaskView> create(Task task) {
		return sview(idev(task.deviceId).model().add(state(), task));
	}

	@Override public Single<TaskView> update(Task task) {
		return sview(itask(task.id).model().replace(state(), task));
	}

	@Override public Single<TaskScheduleView> getSchedule(int taskId) {
		return sscheduleView(itask(taskId));
	}

	@Override public Single<TaskView> statusChange(int taskId, TaskStatusType statusChange) {
		return sview(itask(taskId).changeStatus(state(), statusChange));
	}

	@Override public Single<TaskView> getView(int taskId) {
		return sview(itask(taskId));
	}

	@Override public Observable<TaskView> tasks() {
		return context().tasks().map(TaskRestImpl::view);
	}

	@Override public Single<TaskView> done(int taskId, TaskArg arg, String description) {
		return manual(taskId, t -> t.done(state(), arg, description));
	}

	@Override public Single<TaskView> skip(int taskId) {
		return manual(taskId, t -> t.skip(state()));
	}

	@Override public Single<TaskView> postpone(int taskId, long postponeTo) {
		return manual(taskId, t -> t.postpone(state(),postponeTo));
	}
	
	protected Single<TaskView> manual(int taskId, Consumer<ManualTask> c) {
		ManualTask task = (ManualTask)itask(taskId);
		c.accept(task);
		return sview(task);
	}
	
	protected Single<TaskView> sview(ITask x) { return Single.just(view(x)); }
	protected static TaskView view(ITask task) {
		TaskView tv = new TaskView();
		tv.task = task.model().getTask();
		tv.scheduleView = scheduleView(task);
		return tv;
	}
	
	protected Single<TaskScheduleView> sscheduleView(ITask x) { return Single.just(scheduleView(x)); }
	protected static TaskScheduleView scheduleView(ITask task) {
		TaskStatus status = task.model().getStatus();
		TaskScheduleView sv = new TaskScheduleView();
		sv.taskId = task.getId();
		sv.statusType = status.statusType;
		sv.lastStart = Date.from(task.last().start()).getTime();
		sv.nextStart = Date.from(task.next().start()).getTime();
		sv.on = task.on();
		sv.nextEnd = Date.from(task.last().end()).getTime();
		sv.manualWaitingFron = status.manualWaitingFrom;
		sv.manualPostponedTo = status.manualPostponedTo;
		return sv;
	}

	@Override public Single<String> validateStreamExpr(String streamExpr) {
		return Single.just(new StreamExprParser().parseThis(streamExpr, context()).toShowable());
	}


}
