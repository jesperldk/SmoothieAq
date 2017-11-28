package jesperl.dk.smoothieaq.shared.model.task;

import java.nio.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  MeasurementTaskArg extends TaskArg { 

	public MeasurementType measurementType; 

	@JsOverlay 
	public static MeasurementTaskArg create() {
		MeasurementTaskArg measurementTaskArg = TaskArg_Helper.createMeasurementTaskArg();
		return measurementTaskArg;
	}

	@JsOverlay 
	public static MeasurementTaskArg create(MeasurementType measurementType) {
		MeasurementTaskArg measurementTaskArg = TaskArg_Helper.createMeasurementTaskArg();
		measurementTaskArg.measurementType = measurementType;
		return measurementTaskArg;
	}

	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { MeasurementTaskArg_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public MeasurementTaskArg deserialize(int ver, ByteBuffer in, DbContext context) { return MeasurementTaskArg_Db.deserializeFields(this, in, context); }
}
