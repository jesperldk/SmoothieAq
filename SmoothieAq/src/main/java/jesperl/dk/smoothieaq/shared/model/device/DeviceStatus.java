package jesperl.dk.smoothieaq.shared.model.device;

import java.nio.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  DeviceStatus extends DbWithParrentId implements DeviceStatus_Helper { 
	
//	public short deviceId;
	public DeviceStatusType statusType;

	@JsOverlay 
	public static DeviceStatus create(short deviceId) {
		DeviceStatus deviceStatus = new DeviceStatus();
		deviceStatus.id = deviceId;
		deviceStatus.statusType = DeviceStatusType.disabled;
		return deviceStatus;
	}

	@JsOverlay @GwtIncompatible 
	public static DeviceStatus createS(short deviceId) {
		DeviceStatus deviceStatus = create(deviceId);
		deviceStatus.getDate();
		return deviceStatus;
	}

	@JsOverlay public final DeviceStatus copy() { return DeviceStatus_Db.copy(new DeviceStatus(),this); }
	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { DeviceStatus_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public DeviceStatus deserialize(int ver, ByteBuffer in, DbContext context) { return DeviceStatus_Db.deserializeFields(this, in, context); }
}
  