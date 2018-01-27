package jesperl.dk.smoothieaq.shared.resources;

import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.google.gwt.core.shared.*;
import com.intendia.gwt.autorest.client.*;

import jesperl.dk.smoothieaq.server.task.classes.*;
import jesperl.dk.smoothieaq.shared.model.task.*;
import jsinterop.annotations.*;
import rx.*;
import rx.Observable;

@AutoRestGwt @Path("task") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
public interface TaskRest {  

	@GET Single<Task> get(@QueryParam("id") int taskId);
	@PUT Single<TaskView> create(Task task);
	@POST Single<TaskView> update(Task task);
	
	@GET @Path("schedule") Single<TaskScheduleView> getSchedule(@QueryParam("id") int taskId);
	@PUT @Path("status") Single<TaskView> statusChange(@QueryParam("id") int taskId, TaskStatusType statusChange);

	@GET @Path("view") Single<TaskView> getView(@QueryParam("id") int taskId);
	
	@GET @Path("deviceautotask") Single<TaskView> autoTask(@QueryParam("deviceid") int deviceId);
	@GET @Path("devicemanualtasks") Observable<TaskView> manualTasks(@QueryParam("deviceid") int deviceId);
	@GET @Path("tasks") Observable<TaskView> tasks();

	@POST @Path("done") Single<TaskView> done(@QueryParam("id") int taskId, TaskArg arg, @QueryParam("description") String description);
	@POST @Path("skip") Single<TaskView> skip(@QueryParam("id") int taskId);
	@POST @Path("postpone") Single<TaskView> postpone(@QueryParam("id") int taskId, long postponeTo);
	
	@POST @Path("validatestreamexpr") Single<String> validateStreamExpr(String streamExpr);

	@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
	public static class  TaskView {
		public Task task;
		public TaskScheduleView scheduleView;
	}
	
	@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
	public static class  TaskCompactView {
		public Task task;
		public TaskStatusType statusType;
	}
	
	@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
	public static class  TaskScheduleView {
		public int taskId;
		public TaskStatusType statusType;
		public long lastStart;
		public long nextStart;
		public boolean on;
		public long nextEnd;
		public long manualWaitingFrom;
		public long manualPostponedTo;
	}
	
	@GwtIncompatible public static TaskView view(ITask task) {
		TaskView tv = new TaskView();
		tv.task = task.model().getTask();
		tv.scheduleView = scheduleView(task);
		return tv;
	}
	
	@GwtIncompatible public static TaskCompactView compactView(ITask task) {
		TaskCompactView tv = new TaskCompactView();
		tv.task = task.model().getTask();
		tv.statusType = task.model().getStatus().statusType;
		return tv;
	}
	
	@GwtIncompatible public static TaskScheduleView scheduleView(ITask task) {
		TaskStatus status = task.model().getStatus();
		TaskScheduleView sv = new TaskScheduleView();
		sv.taskId = task.getId();
		sv.statusType = status.statusType;
		sv.lastStart = Date.from(task.last().start()).getTime();
		sv.nextStart = Date.from(task.next().start()).getTime();
		sv.on = task.on();
		sv.nextEnd = Date.from(task.last().end()).getTime();
		sv.manualWaitingFrom = status.manualWaitingFrom;
		sv.manualPostponedTo = status.manualPostponedTo;
		return sv;
	}
}
