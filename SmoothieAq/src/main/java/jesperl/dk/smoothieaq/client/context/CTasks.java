package jesperl.dk.smoothieaq.client.context;

import static jesperl.dk.smoothieaq.client.context.CContext.*;
import static jesperl.dk.smoothieaq.shared.model.task.TaskType.*;

import java.util.*;

import jesperl.dk.smoothieaq.client.*;
import jesperl.dk.smoothieaq.shared.resources.TaskRest.*;
import rx.*;
import rx.Observable;
import rx.subjects.*;

public class CTasks {
	
	private Map<Integer, CTask> idToTask = new HashMap<>();
	private Set<Integer> dueTaskIds = new HashSet<>();

	private Subject<Set<Integer>, Set<Integer>> dueTasksSubject = PublishSubject.create();
	public final Observable<Set<Integer>> dueTasks = dueTasksSubject.replay(1).autoConnect();

	private Completable ready = Resources.task.tasks()
			.doOnNext(tv -> { taskChanged(tv.compactView); scheduleChanged(tv.scheduleView); }) 
			.count().cache().toCompletable();

	private final Subject<CTask, CTask> tasksSubject = PublishSubject.create();
	
	public CTasks() {
		dueTasks.subscribe();
		dueTasksSubject.onNext(new HashSet<>(dueTaskIds));
	}
	
	public Single<CTask> task(int id) {
		return ready.toSingle(() -> idToTask.get(id));
	}
	
	public Observable<CTask> tasks() {
		return ready.andThen(Observable.from(idToTask.values())).concatWith(tasksSubject);
	}
	
	public void taskChanged(TaskCompactView compactView) {
		CTask cTask = idToTask.get(compactView.task.id);
		if (cTask == null) {
			CTask newTask = new CTask(compactView.task.id);
			cTask = newTask;
			idToTask.put(compactView.task.id, cTask);
			tasksSubject.onNext(cTask);
			ctx.cDevices.device(compactView.task.deviceId).subscribe(cd -> {
				if (compactView.task.taskType().get().isOfType(auto)) {
					cd.autoTasksSubject.onNext(newTask); //GWT.log("set autoTask on "+cd.getCurrentCompactView().name);
				} else {
					cd.manualTasksSubject.onNext(newTask); //GWT.log("and manualTask on "+cd.getCurrentCompactView().name);
				}
				newTask.cDeviceX = cd;
			});
		}
		cTask.compactViewSubject.onNext(compactView);
	}
	
	public void scheduleChanged(TaskScheduleView scheduleView) {
		CTask cTask = idToTask.get(scheduleView.taskId);
		if (cTask == null) return;
		cTask.scheduleViewSubject.onNext(scheduleView);
		if (scheduleView.manualWaitingFrom != 0 && !dueTaskIds.contains(scheduleView.taskId)) {
			dueTaskIds.add(scheduleView.taskId);
			dueTasksSubject.onNext(new HashSet<>(dueTaskIds));
		} else if (scheduleView.manualWaitingFrom == 0 && dueTaskIds.contains(scheduleView.taskId)) {
			dueTaskIds.remove(scheduleView.taskId);
			dueTasksSubject.onNext(new HashSet<>(dueTaskIds));
		}
	}
	
}
