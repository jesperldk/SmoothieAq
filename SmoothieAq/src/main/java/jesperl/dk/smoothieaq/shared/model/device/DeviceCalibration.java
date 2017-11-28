package jesperl.dk.smoothieaq.shared.model.device;

import java.nio.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  DeviceCalibration extends DbWithParrentId {
	
//	public short deviceId;
	public float[] values;

	@JsOverlay 
	static public DeviceCalibration create(short deviceId) {
		DeviceCalibration deviceCalibration = new DeviceCalibration();
		deviceCalibration.id = deviceId;
		return deviceCalibration; 
	}

	@JsOverlay @GwtIncompatible 
	public static DeviceCalibration createS(short deviceId) {
		DeviceCalibration deviceCalibration = create(deviceId);
		deviceCalibration.getDate();
		return deviceCalibration;
	}

	@JsOverlay public final DeviceCalibration copy() { return DeviceCalibration_Db.copy(new DeviceCalibration(),this); }
	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { DeviceCalibration_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public DeviceCalibration deserialize(int ver, ByteBuffer in, DbContext context) { return DeviceCalibration_Db.deserializeFields(this, in, context); }
}
