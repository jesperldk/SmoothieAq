package jesperl.dk.smoothieaq.shared.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.intendia.gwt.autorest.client.*;

import jesperl.dk.smoothieaq.shared.model.task.*;
import jsinterop.annotations.*;
import rx.*;

@AutoRestGwt @Path("task") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
public interface TaskRest {  

	@GET Single<Task> get(@QueryParam("id") int taskId);
	@PUT Single<TaskView> create(Task task);
	@POST Single<TaskView> update(Task task);
	
	@GET @Path("status") Single<TaskScheduleView> getSchedule(@QueryParam("id") int taskId);
	@PUT @Path("status") Single<TaskView> statusChange(@QueryParam("id") int taskId, TaskStatusType statusChange);

	@GET @Path("view") Single<TaskView> getView(@QueryParam("id") int taskId);
	
	@GET @Path("devices") Observable<TaskView> tasks();

	@POST @Path("done") Single<TaskView> done(@QueryParam("id") int taskId, TaskArg arg, @QueryParam("description") String description);
	@POST @Path("skip") Single<TaskView> skip(@QueryParam("id") int taskId);
	@POST @Path("postpone") Single<TaskView> postpone(@QueryParam("id") int taskId, long postponeTo);
	
	@GET @Path("validatestreamexpr") Single<String> validateStreamExpr(String streamExpr);

	@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
	public static class TaskView {
		public Task task;
		public TaskScheduleView scheduleView;
	}
	
	@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
	public static class TaskScheduleView {
		public short taskId;
		public TaskStatusType statusType;
		public long lastStart;
		public long nextStart;
		public boolean on;
		public long nextEnd;
		public long manualWaitingFron;
		public long manualPostponedTo;
	}
	
}
