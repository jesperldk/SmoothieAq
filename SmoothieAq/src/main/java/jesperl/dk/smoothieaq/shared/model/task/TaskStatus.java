package jesperl.dk.smoothieaq.shared.model.task;

import java.nio.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  TaskStatus extends DbWithParrentId implements TaskStatus_Helper {
	
//	public short taskId;
	public TaskStatusType statusType;
	public long manualWaitingFrom;
	public long manualPostponedTo;
	
	@JsOverlay 
	public static TaskStatus create(int taskId) {
		TaskStatus taskStatus = new TaskStatus();
		taskStatus.id = taskId;
		taskStatus.statusType = TaskStatusType.enabled;
		return taskStatus;
	}

	@JsOverlay @GwtIncompatible 
	public static TaskStatus createS(int taskId) {
		TaskStatus taskStatus = create(taskId);
		taskStatus.getDate();
		return taskStatus;
	}

	@JsOverlay public final TaskStatus copy() { return TaskStatus_Db.copy(new TaskStatus(),this); } 
	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { TaskStatus_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public TaskStatus deserialize(int ver, ByteBuffer in, DbContext context) { return TaskStatus_Db.deserializeFields(this, in, context); }
}
