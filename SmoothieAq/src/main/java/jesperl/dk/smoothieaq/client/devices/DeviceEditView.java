package jesperl.dk.smoothieaq.client.devices;

import static jesperl.dk.smoothieaq.client.context.CContext.*;
import static jesperl.dk.smoothieaq.client.text.AppMessages.*;

import static jesperl.dk.smoothieaq.client.components.GuiUtil.*;

import gwt.material.design.client.ui.*;
import gwt.material.design.client.ui.html.*;
import jesperl.dk.smoothieaq.shared.model.device.*;

public class DeviceEditView extends Div {

	public DeviceEditView(Device device, boolean newDevice) {
    	add(new MaterialTitle(newDevice ? appMsg.newDeviceTitle() : appMsg.editDeviceTitle()));
    	MaterialPanel panel = new MaterialPanel();
    	panel.add(wRo(wComboBox(device.deviceType()),!newDevice));
    	panel.add(wRo(wComboBox(device.deviceClass()),!newDevice));
    	panel.add(wComboBox(device.deviceCategory()));
    	panel.add(wRo(wComboBox(device.driverId(),ctx.cDrivers.options()),!newDevice));
    	panel.add(wRo(wTextBox(device.deviceUrl()),!newDevice));
    	panel.add(wTextBox(device.name()));
    	panel.add(wTextBox(device.description()));
    	panel.add(wRo(wComboBox(device.measurementType()),!newDevice));
    	panel.add(wFloatBox(device.repeatabilityLevel()));
    	panel.add(wFloatBox(device.onLevel()));
    	panel.add(wFloatBox(device.wattAt100pct()));
    	add(panel);
	}
}
