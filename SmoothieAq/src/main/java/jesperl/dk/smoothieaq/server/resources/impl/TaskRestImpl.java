package jesperl.dk.smoothieaq.server.resources.impl;

import java.util.function.*;

import jesperl.dk.smoothieaq.server.streamexpr.*;
import jesperl.dk.smoothieaq.server.task.classes.*;
import jesperl.dk.smoothieaq.shared.model.task.*;
import jesperl.dk.smoothieaq.shared.resources.*;
import rx.*;

public class  TaskRestImpl extends RestImpl implements TaskRest {

	protected ITask itask(int taskId) { return context().getWTask(taskId); }

	@Override public Single<Task> get(int taskId) {
		return funcGuarded(() -> Single.just(itask(taskId).model().getTask()));
	}

	@Override public Single<TaskView> create(Task task) {
		return funcGuarded(() -> sview(idev(task.deviceId).model().add(state(), task)));
	}

	@Override public Single<TaskView> update(Task task) {
		return funcGuarded(() -> sview(itask(task.id).model().replace(state(), task)));
	}

	@Override public Single<TaskScheduleView> getSchedule(int taskId) {
		return funcGuarded(() -> sscheduleView(itask(taskId)));
	}

	@Override public Single<TaskView> statusChange(int taskId, TaskStatusType statusChange) {
		return funcGuarded(() -> sview(itask(taskId).changeStatus(state(), statusChange)));
	}

	@Override public Single<TaskView> getView(int taskId) {
		return funcGuarded(() -> sview(itask(taskId)));
	}

	@Override public Single<TaskView> autoTask(int deviceId) {
		return funcGuarded(() -> Observable.from(idev(deviceId).model().getTasks()).filter(it -> it.model().getTask().taskType.isOfType(TaskType.auto)).map(TaskRest::view).toSingle());
	}
	@Override public Observable<TaskView> manualTasks(int deviceId) {
		return funcGuarded(() -> Observable.from(idev(deviceId).model().getTasks()).filter(it -> !it.model().getTask().taskType.isOfType(TaskType.auto)).map(TaskRest::view));
	}
	@Override public Observable<TaskCompactView> tasks() {
		return funcGuarded(() -> context().tasks().map(TaskRest::compactView));
	}

	@Override public Single<TaskView> done(int taskId, TaskArg arg, String description) {
		return funcGuarded(() -> manual(taskId, t -> t.done(state(), arg, description)));
	}

	@Override public Single<TaskView> skip(int taskId) {
		return funcGuarded(() -> manual(taskId, t -> t.skip(state())));
	}

	@Override public Single<TaskView> postpone(int taskId, long postponeTo) {
		return funcGuarded(() -> manual(taskId, t -> t.postpone(state(),postponeTo)));
	}
	
	@Override public Single<String> validateStreamExpr(String streamExpr) {
		return funcGuarded(() -> Single.just(new StreamExprParser().parseThis(streamExpr, context()).toShowable()));
	}

	protected Single<TaskView> manual(int taskId, Consumer<ManualTask> c) {
		ManualTask task = (ManualTask)itask(taskId);
		c.accept(task);
		return sview(task);
	}
	
	protected Single<TaskView> sview(ITask task) { return Single.just(TaskRest.view(task)); }
	
	protected Single<TaskScheduleView> sscheduleView(ITask task) { return Single.just(TaskRest.scheduleView(task)); }

}
