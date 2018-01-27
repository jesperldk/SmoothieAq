package jesperl.dk.smoothieaq.shared.model.event;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.task.classes.*;
import jesperl.dk.smoothieaq.shared.resources.*;
import jesperl.dk.smoothieaq.shared.resources.TaskRest.*;
import jsinterop.annotations.*;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  TaskChangeEvent extends Event implements MessageEvent_Helper { 

	public TaskCompactView compactView;

	@GwtIncompatible public static TaskChangeEvent create(ITask task) {
		return create(TaskRest.compactView(task));
	}
	
	@GwtIncompatible public static TaskChangeEvent create(TaskCompactView compactView) {
		TaskChangeEvent event = new TaskChangeEvent();
		event.compactView = compactView;
		return event;
	}

}
