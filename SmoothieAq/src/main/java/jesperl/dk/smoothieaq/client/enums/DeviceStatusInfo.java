package jesperl.dk.smoothieaq.client.enums;

import static gwt.material.design.client.constants.Color.*;
import static gwt.material.design.client.constants.IconType.*;
import static java.util.Collections.*;
import static jesperl.dk.smoothieaq.client.ClientObjects.*;
import static jesperl.dk.smoothieaq.shared.model.device.DeviceStatusChange.*;
import static jesperl.dk.smoothieaq.shared.model.device.DeviceStatusType.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.list;

import java.util.*;

import gwt.material.design.client.constants.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.util.shared.*;
import rx.functions.*;

public class DeviceStatusInfo extends EnumInfo {
	public final List<DeviceStatusChange> legalChanges;
	
	public DeviceStatusInfo(IconType icon, Color bgColor, String hoverTxt, List<DeviceStatusChange> legalChanges) {
		super(icon, bgColor, hoverTxt);
		this.legalChanges = unmodifiableList(legalChanges);
	}
	
	public static Pair<DeviceStatusType, DeviceStatusInfo> dsi(DeviceStatusType type, IconType icon, Color bgColor, List<DeviceStatusChange> legalChanges) { 
		return p(type, new DeviceStatusInfo(icon, bgColor, type.toString(), legalChanges)); 
	}

	public static Func1<DeviceStatusType, DeviceStatusInfo> func = func(DeviceStatusType.class,
			dsi(enabled,	PLAY_ARROW,	GREEN_ACCENT_3,	list(pause,disable,delete)),
			dsi(paused,		PAUSE,		TEAL_ACCENT_3,	list(unpause,disable,delete)),
			dsi(disabled,	CLOSE,		RED_ACCENT_3,	list(enable,delete)),
			dsi(deleted,	DELETE,		PURPLE_ACCENT_3,list())
	);

}