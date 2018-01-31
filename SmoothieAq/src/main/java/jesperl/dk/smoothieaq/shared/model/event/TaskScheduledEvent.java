package jesperl.dk.smoothieaq.shared.model.event;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.task.classes.*;
import jesperl.dk.smoothieaq.shared.resources.*;
import jesperl.dk.smoothieaq.shared.resources.TaskRest.*;
import jsinterop.annotations.*;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  TaskScheduledEvent extends Event implements MessageEvent_Helper { 

	public TaskScheduleView scheduleView;

	@GwtIncompatible public static TaskScheduledEvent create(ITask task) {
		return create(TaskRest.scheduleView(task));
	}
	
	@GwtIncompatible public static TaskScheduledEvent create(TaskScheduleView scheduleView) {
		TaskScheduledEvent event = new TaskScheduledEvent();
		event.scheduleView = scheduleView;
		return event;
	}

}
