package jesperl.dk.smoothieaq.shared.model.measure;

import java.nio.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class Measure extends DbWithStamp {
	
	public short deviceId;
	public DeviceStream stream;
	public float value;
	
	@JsOverlay 
	public static Measure create(short deviceId, DeviceStream stream, float value) {
		Measure measure = new Measure();
		measure.getDate(); // trigger stamp
		measure.deviceId = deviceId;
		measure.stream = stream;
		measure.value = value;
		return measure;
	}

	@Override @JsOverlay public Measure copy() { return Measure_Db.copy(new Measure(),this); }
	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { Measure_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public Measure deserialize(int ver, ByteBuffer in, DbContext context) { return Measure_Db.deserializeFields(this, in, context); }
}
