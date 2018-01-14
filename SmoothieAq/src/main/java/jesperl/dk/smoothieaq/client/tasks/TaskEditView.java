package jesperl.dk.smoothieaq.client.tasks;

import static jesperl.dk.smoothieaq.client.context.CContext.*;

import com.google.gwt.core.client.*;

import static jesperl.dk.smoothieaq.client.components.GuiUtil.*;

import gwt.material.design.client.ui.*;
import gwt.material.design.client.ui.html.*;
import jesperl.dk.smoothieaq.client.components.*;
import jesperl.dk.smoothieaq.shared.model.task.*;
import jesperl.dk.smoothieaq.shared.model.task.TaskTypeUtil.*;

public class TaskEditView extends Div {

	public TaskEditView(Task task, boolean newTask) {
    	add(new MaterialTitle("Task for device "+ctx.cDevices.name(task.deviceId))); // TODO
    	MaterialPanel panel = new MaterialPanel();
    	panel.add(wRo(wComboBox(task.taskType()),!newTask));
    	panel.add(new WSingle(task.taskType().listen().doOnNext(tt -> GWT.log("got one "+tt)).map(tt -> {
        	MaterialPanel panel2 = new MaterialPanel();
    		if (tt == null) return panel2;
    		TaskTypeInfo ttInfo = tt.info();
    		if (ttInfo.taskArg != null) {
    			panel2.add(new MaterialLabel("taskArg")); // TODO
    		}
    		if (ttInfo.whenAllowed) panel2.add(wTextBox(task.whenStream()));
    		if (ttInfo.intervalSchedule) {
    			panel2.add(new MaterialLabel("schedule")); // TODO
    		}
    		return panel2;
    	})));
//    	panel.add(wRo(wComboBox(device.deviceClass()),!newDevice));
//    	panel.add(wComboBox(device.deviceCategory()));
//    	panel.add(wRo(wComboBox(device.driverId(),ctx.cDrivers.options()),!newDevice));
//    	panel.add(wTextBox(device.deviceUrl()));
//    	panel.add(wTextBox(device.name()));
//    	panel.add(wTextBox(device.description()));
//    	panel.add(wRo(wComboBox(device.measurementType()),!newDevice));
//    	panel.add(wFloatBox(device.repeatabilityLevel()));
//    	panel.add(wFloatBox(device.onLevel()));
//    	panel.add(wFloatBox(device.wattAt100pct()));
    	add(panel);
	}
}
