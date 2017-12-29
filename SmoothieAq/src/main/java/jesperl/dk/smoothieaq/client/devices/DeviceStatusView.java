package jesperl.dk.smoothieaq.client.devices;

import static jesperl.dk.smoothieaq.client.components.GuiUtil.*;

import gwt.material.design.client.constants.*;
import gwt.material.design.client.ui.*;
import jesperl.dk.smoothieaq.client.enums.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.resources.DeviceRest.*;
import rx.functions.*;

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
		
		Func1<DeviceStatusType, DeviceStatusInfo> func = DeviceStatusInfo.func;
		DeviceStatusInfo dsi = func.call(device.statusType);
		add(wFloatButton(dsi, null));
		
		MaterialFABList fabl = new MaterialFABList();
		dsi.legalChanges.forEach(c -> {
			DeviceChangeInfo dci = DeviceChangeInfo.func.call(c);
			fabl.add(wFloatButton(dci, () -> dci.doChange.call(device.deviceId)));
		});
		add(fabl);
	}
	
	
}
