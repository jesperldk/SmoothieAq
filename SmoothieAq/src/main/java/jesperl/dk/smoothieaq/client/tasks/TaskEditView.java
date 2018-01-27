package jesperl.dk.smoothieaq.client.tasks;

import static jesperl.dk.smoothieaq.client.inheritancetypes.TaskArgInfo.*;

import java.util.*;

import static jesperl.dk.smoothieaq.client.components.GuiUtil.*;

import gwt.material.design.client.ui.*;
import gwt.material.design.client.ui.html.*;
import jesperl.dk.smoothieaq.client.components.*;
import jesperl.dk.smoothieaq.client.context.*;
import jesperl.dk.smoothieaq.client.inheritancetypes.*;
import jesperl.dk.smoothieaq.shared.model.db.fields.*;
import jesperl.dk.smoothieaq.shared.model.schedule.*;
import jesperl.dk.smoothieaq.shared.model.task.*;
import jesperl.dk.smoothieaq.shared.model.task.TaskTypeUtil.*;
import jesperl.dk.smoothieaq.shared.resources.DeviceRest.*;

public class TaskEditView extends Div {

	public TaskEditView(CDevice cDevice, Task task, boolean newTask, boolean autoTask) {
		DeviceCompactView cv = cDevice.getCurrentCompactView();
    	add(new MaterialTitle((autoTask ? "Schedule for " : "Task for device ")+cv.name)); // TODO
    	
    	Set<TaskType> types = autoTask ? TaskTypeUtil.autoTypes(cv.deviceClass) : TaskTypeUtil.manualTypes(cv.deviceType);
    	
    	MaterialPanel panel = new MaterialPanel();
    	panel.add(wComboBox(task.taskType(), wOptions(types)));
    	panel.add(new WSingle(task.taskType().listen().map(tt -> {
        	MaterialPanel panel2 = new MaterialPanel();
    		if (tt == null) return panel2;
    		TaskTypeInfo ttInfo = tt.info();
    		
    		if (ttInfo.taskArg != null) {
    			if (task.taskArg == null || !task.taskArg.$type.equals(ttInfo.taskArg.$type)) task.taskArg = ttInfo.taskArg.copy();
    			info(task.taskArg).addFields(task.taskArg, panel2);
    		}
    		
    		if (ttInfo.whenAllowed) panel2.add(wTextBox(task.whenStream()));
    		
    		Set<String> scheduleTypes = 
    				(!autoTask)					? ScheduleInfo.manualPoints :
    				(ttInfo.intervalSchedule) 	? ScheduleInfo.autoSchedules :
    											  ScheduleInfo.autoPoints;
    		StringField scheduleType = new StringField("Schedule.type");
    		if (task.schedule != null && scheduleTypes.contains(task.schedule.$type)) scheduleType.set(task.schedule.$type);
    		
    		panel2.add(wComboBox(scheduleType, wTypeOptions(scheduleTypes)));
    		panel2.add(new WSingle(scheduleType.listen().map(st -> {
            	MaterialPanel panel3 = new MaterialPanel();
        		if (st == null) return panel3;
        		
        		InheritanceTypeInfo<Schedule> sInfo = ScheduleInfo.infos.get(scheduleType.get());
       			if (task.schedule == null || !task.schedule.$type.equals(scheduleType.get())) task.schedule = sInfo.create();
       			sInfo.addFields(task.schedule, panel3);
       		
        		return panel3;
        	})));
       		return panel2;
    	})));
    	add(panel);
	}
}
