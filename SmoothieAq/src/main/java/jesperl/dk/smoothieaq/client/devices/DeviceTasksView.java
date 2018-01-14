package jesperl.dk.smoothieaq.client.devices;

import static jesperl.dk.smoothieaq.client.ClientObjects.*;

import static jesperl.dk.smoothieaq.client.components.GuiUtil.*;

import gwt.material.design.client.constants.*;
import gwt.material.design.client.ui.*;
import gwt.material.design.client.ui.html.*;
import jesperl.dk.smoothieaq.client.*;
import jesperl.dk.smoothieaq.client.tasks.*;
import jesperl.dk.smoothieaq.shared.model.task.*;
import jesperl.dk.smoothieaq.shared.resources.DeviceRest.*;

public class DeviceTasksView extends Div {

	public DeviceTasksView(DeviceView deviceView) {
    	add(new MaterialTitle("Tasks for device "+deviceView.device.name)); // TODO
    	MaterialPanel panel = new MaterialPanel();
//    	panel.add(wRo(wComboBox(device.deviceType()),!newDevice));
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
    	panel.add(wButton(IconType.ADD, true, "Add new task", null, () -> {
    		Task task = new Task();
    		task.deviceId = deviceView.device.id;
    		wModal(new TaskEditView(task, true), () -> Resources.task.create(task).subscribe(robs()));
    	}));
    	add(panel);
	}
}
