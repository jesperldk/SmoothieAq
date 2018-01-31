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
	
	private Completable ready = Resources.task.tasks()
			.doOnNext(tv -> { taskChanged(tv.compactView); scheduleChanged(tv.scheduleView); }) 
			.count().cache().toCompletable();

	private final Subject<CTask, CTask> tasksSubject = PublishSubject.create();
	
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
				if (compactView.task.taskType().get().isOfType(auto)) cd.autoTaskX = newTask;
				else cd.manualTasksSubject.onNext(newTask);
				newTask.cDeviceX = cd;
			});
		}
		cTask.compactViewSubject.onNext(compactView);
	}
	
	public void scheduleChanged(TaskScheduleView scheduleView) {
		CTask cTask = idToTask.get(scheduleView.taskId);
		if (cTask == null) return;
		cTask.scheduleViewSubject.onNext(scheduleView);
	}
	
//	public Function<Float,String> formatter(int deviceId, short streamId) {
//		return nnv(funcNotNull(idToDevice.get(deviceId), cd -> cd.formatter(streamId)), v -> strv(v));
//	}
//	public String name(int deviceId) {
//		if (deviceId == 0) return "";
//		return idToName.get(deviceId); 
//	} 
//	public String name(int deviceId, short streamId) { 
//		if (deviceId == 0) return "";
//		if (streamId == 0) return idToName.get(deviceId);
//		return idToName.get(deviceId)+"."+fromId.get(streamId).name(); 
//	} 
}
