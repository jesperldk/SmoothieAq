package jesperl.dk.smoothieaq.client.text;

import com.google.gwt.core.client.*;
import com.google.gwt.i18n.client.*;

public interface AppMessages extends Messages {
	public static AppMessages appMsg = GWT.create(AppMessages.class );
	
	@DefaultMessage("Right now")
	String menuRightnow();

	@DefaultMessage("Devices")
	String menuDevices();
	
	
	@DefaultMessage("Show graph")
	String deviceGraph();
	
	@DefaultMessage("No errors")
	String deviceError();
	
	@DefaultMessage("Show errors")
	String deviceErrorNone();
	
	@DefaultMessage("Show and edit alarm")
	String deviceAlarm();
	
	@DefaultMessage("Show and edit schedule")
	String deviceSchedule();
	
	@DefaultMessage("Show and edit tasks")
	String deviceTasks();

	@DefaultMessage("Edit device name and setting")
	String deviceEdit();
	
	
	@DefaultMessage("Do the task")
	String taskDo();
	
	@DefaultMessage("Edit task")
	String taskEdit();
	
	@DefaultMessage("Task is enabled, click to change")
	String taskEnabled();
	
	@DefaultMessage("Task is enabled but the device is not, click to change status for task")
	String taskEnabledDeviceNot();
	
	@DefaultMessage("Task is paused, click to change")
	String taskPaused();
	
	@DefaultMessage("Task is deleted")
	String taskDeleted();
	
	@DefaultMessage("Enable task")
	String taskEnable();
	
	@DefaultMessage("Pause task")
	String taskPause();
	
	@DefaultMessage("Delete task")
	String taskDelete();
	
	
	@DefaultMessage("Edit")
	String edit();
	
	@DefaultMessage("View")
	String view();
	
	@DefaultMessage("Ok")
	String ok();
	
	@DefaultMessage("Tasks")
	String tasks();
	
	@DefaultMessage("Cancel")
	String cancel();
	
	@DefaultMessage("Close")
	String close();
	

	@DefaultMessage("New device")
	String newDeviceTitle();

	@DefaultMessage("Edit device")
	String editDeviceTitle();
}
