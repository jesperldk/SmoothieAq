package jesperl.dk.smoothieaq.shared.model.event;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.device.classes.*;
import jesperl.dk.smoothieaq.shared.resources.*;
import jesperl.dk.smoothieaq.shared.resources.DeviceRest.*;
import jsinterop.annotations.*;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  DeviceChangeEvent extends Event implements MessageEvent_Helper { 

	public DeviceCompactView compactView;

	@GwtIncompatible public static DeviceChangeEvent create(IDevice device) {
		return create(DeviceRest.compactView(device));
	}
	
	@GwtIncompatible public static DeviceChangeEvent create(DeviceCompactView compactView) {
		DeviceChangeEvent event = new DeviceChangeEvent();
		event.compactView = compactView;
		return event;
	}

}
