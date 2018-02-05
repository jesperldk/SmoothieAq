package jesperl.dk.smoothieaq.client.context;

import static jesperl.dk.smoothieaq.shared.model.task.TaskStatusType.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import jesperl.dk.smoothieaq.shared.model.db.*;
import jesperl.dk.smoothieaq.shared.model.task.*;
import jesperl.dk.smoothieaq.shared.resources.TaskRest.*;
import jesperl.dk.smoothieaq.util.shared.*;
import rx.*;
import rx.subjects.*;

public class CTask {
	
	public final int id;
	private TaskCompactView currentCompactView;
	private TaskScheduleView currentScheduleView;
	
	/*friend*/ Subject<TaskCompactView, TaskCompactView> compactViewSubject = PublishSubject.create();
	public final Observable<Pair<CTask,TaskCompactView>> compactView = compactViewSubject.map(cv -> pair(this,fixup(cv))).replay(1).autoConnect();
	/*friend*/ Subject<TaskScheduleView, TaskScheduleView> scheduleViewSubject = PublishSubject.create();
	public final Observable<TaskScheduleView> scheduleView = scheduleViewSubject.map(sv -> fixup(sv)).replay(1).autoConnect();
	
	/*friend*/ CDevice cDeviceX;
	public final Single<CDevice> cDevice = Single.fromCallable(() -> cDeviceX);

	/*friend*/ CTask(int id) { 
		this.id = id;
		compactView.map(Pair::getB).doOnNext(cv -> {
			currentCompactView = cv;
		}).first().subscribe(cv -> { // also gets it running hot...
//			setupStream(cv);
		});
		scheduleView.doOnNext(sv -> {
			currentScheduleView = sv;
		}).first().subscribe(cv -> { // also gets it running hot...
//			setupStream(cv);
		});
	}

	public boolean isNotDeleted() { return currentCompactView == null || currentCompactView.statusType != deleted; }

	private TaskScheduleView fixup(TaskScheduleView sv) {
		return sv;
	}

	private TaskCompactView fixup(TaskCompactView cv) {
		cv.statusType 		= EnumField.fixup(TaskStatusType.class, cv.statusType);
		cv.task.taskType	= EnumField.fixup(TaskType.class, cv.task.taskType);
		return cv;
	}

	public TaskCompactView getCurrentCompactView() { return currentCompactView; }
	public TaskScheduleView getCurrentScheduleView() { return currentScheduleView; }
	
}
