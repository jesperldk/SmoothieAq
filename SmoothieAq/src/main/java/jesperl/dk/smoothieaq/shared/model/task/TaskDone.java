package jesperl.dk.smoothieaq.shared.model.task;

import java.nio.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  TaskDone extends DbWithParrentId implements TaskDone_Helper { 
	
//	public short taskId;
	public long start;
	public long end;
	public boolean manualNotDone; 
	public TaskArg taskArg;
	public String description;
	
	@JsOverlay 
	public static TaskDone create(int taskId) {
		TaskDone taskDone = new TaskDone();
		taskDone.id = taskId;
		return taskDone;
	}

	@JsOverlay @GwtIncompatible 
	public static TaskDone createS(int taskId) {
		TaskDone taskDone = create(taskId);
		taskDone.getDate();
		return taskDone;
	}

	@JsOverlay public final TaskDone copy() { return TaskDone_Db.copy(new TaskDone(),this); }
	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { TaskDone_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public TaskDone deserialize(int ver, ByteBuffer in, DbContext context) { return TaskDone_Db.deserializeFields(this, in, context); }
}
