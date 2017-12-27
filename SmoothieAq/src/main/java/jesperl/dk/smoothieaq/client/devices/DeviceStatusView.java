package jesperl.dk.smoothieaq.client.devices;

import static gwt.material.design.client.constants.IconType.*;
import static jesperl.dk.smoothieaq.shared.model.db.EnumField.*;
import static jesperl.dk.smoothieaq.shared.model.device.DeviceStatusChange.*;
import static jesperl.dk.smoothieaq.shared.model.device.DeviceStatusType.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.util.*;

import static jesperl.dk.smoothieaq.client.components.GuiUtil.*;

import gwt.material.design.client.constants.*;
import gwt.material.design.client.ui.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.resources.DeviceRest.*;

public class DeviceStatusView extends MaterialFAB {
//	enabled "play_arrow"
//	paused "pause"
//	disabled "close"
//	deleted "delete"
//	
//	primary "star"
//	secondary "star_border"
//	system "developer_board"
//	manual "pan_tool"
//	external "domain"
//	calculated "dialpad"
	
	public DeviceStatusView(DeviceCompactView device) {
		setStyle("position: absolute");
		add(new MaterialAnchorButton(ButtonType.FLOATING, deviceStatusText(device.statusType), wIcon(deviceStatusIcon(device.statusType))));
		MaterialFABList fabl = new MaterialFABList();
		deviceChanges(device.statusType).forEach(c -> fabl.add(new MaterialAnchorButton(ButtonType.FLOATING, deviceChangeText(c), wIcon(deviceChangeIcon(c)))));
		add(fabl);
	}
	
	private static Map<DeviceStatusType, IconType> typeToIcon = map(new HashMap<>(),
			enabled,PLAY_ARROW, paused,PAUSE, disabled,CLOSE, deleted,DELETE
	);
	public static IconType deviceStatusIcon(DeviceStatusType type) { return typeToIcon.get(fixup(DeviceStatusType.class, type)); }
	public static String deviceStatusText(DeviceStatusType type) { return typeToIcon.toString(); }
	
	private static Map<DeviceStatusChange, IconType> changeToIcon = map(new HashMap<>(),
			enable,PLAY_ARROW, unpause,PLAY_ARROW, pause,PAUSE, disable,CLOSE, delete,DELETE
	);
	public static IconType deviceChangeIcon(DeviceStatusChange change) { return changeToIcon.get(fixup(DeviceStatusChange.class, change)); }
	public static String deviceChangeText(DeviceStatusChange change) { return changeToIcon.toString(); }

	
	private static Map<DeviceStatusType, List<DeviceStatusChange>> typeToChange = map(new HashMap<>(),
			enabled,list(pause,disable,delete), paused,list(unpause,disable,delete), disabled,list(enable,delete), deleted,list()
	);
	public static List<DeviceStatusChange> deviceChanges(DeviceStatusType type) { return typeToChange.get(fixup(DeviceStatusType.class, type)); }
}
