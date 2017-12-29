package jesperl.dk.smoothieaq.client.enums;

import static gwt.material.design.client.constants.Color.*;
import static gwt.material.design.client.constants.IconType.*;
import static jesperl.dk.smoothieaq.client.ClientObjects.*;
import static jesperl.dk.smoothieaq.shared.model.device.DeviceStatusChange.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import gwt.material.design.client.constants.*;
import jesperl.dk.smoothieaq.client.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.util.shared.*;
import rx.functions.*;

public class DeviceChangeInfo extends EnumInfo {
	public final Action1<Short> doChange;
	
	public DeviceChangeInfo(IconType icon, Color bgColor, String hoverTxt, Action1<Short> doChange) {
		super(icon, bgColor, hoverTxt);
		this.doChange = doChange;
	}
	
	public static Pair<DeviceStatusChange, DeviceChangeInfo> dci(DeviceStatusChange change, IconType icon, Color bgColor, Action1<Short> doChange) { 
		return p(change, new DeviceChangeInfo(icon, bgColor, change.toString(), doChange)); 
	}
	public static Pair<DeviceStatusChange, DeviceChangeInfo> dci(DeviceStatusChange change, IconType icon, Color bgColor) { 
		return p(change, new DeviceChangeInfo(icon, bgColor, change.toString(), deviceId -> Resources.device.statusChange(deviceId, change).subscribe())); 
	}

	public static Func1<DeviceStatusChange, DeviceChangeInfo> func = func(DeviceStatusChange.class,
			dci(enable,		PLAY_ARROW,	GREEN_ACCENT_2),
			dci(unpause,	PLAY_ARROW,	GREEN_ACCENT_2),
			dci(pause,		PAUSE,		TEAL_ACCENT_2),
			dci(disable,	CLOSE,		RED_ACCENT_2),
			dci(delete,		DELETE,		PURPLE_ACCENT_2)
	);
}