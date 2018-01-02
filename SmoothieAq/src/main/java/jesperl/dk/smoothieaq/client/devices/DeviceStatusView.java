package jesperl.dk.smoothieaq.client.devices;

import static jesperl.dk.smoothieaq.client.enums.DeviceChangeInfo.*;
import static jesperl.dk.smoothieaq.client.enums.DeviceStatusInfo.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import static jesperl.dk.smoothieaq.client.components.GuiUtil.*;

import gwt.material.design.client.constants.*;
import gwt.material.design.client.ui.*;
import jesperl.dk.smoothieaq.client.enums.*;
import jesperl.dk.smoothieaq.shared.resources.DeviceRest.*;

public class DeviceStatusView extends MaterialFAB {
//	primary "star"
//	secondary "star_border"
//	system "developer_board"
//	manual "pan_tool"
//	external "domain"
//	calculated "dialpad"
	
	public DeviceStatusView(DeviceCompactView device) {
		setStyle("position: absolute");
		setAxis(Axis.HORIZONTAL);
		
		DeviceStatusInfo dsi = dsinfo.call(device.statusType);
		add(wFloatButton(dsi, null));
		
		MaterialFABList fablList = new MaterialFABList();
		dsi.legalChanges.forEach(c -> with(dcinfo.call(c), dci -> fablList.add(wFloatButton(dci, () -> dci.doChange.call(device.deviceId)))));
		add(fablList);
	}
	
	
}
