package jesperl.dk.smoothieaq.shared.model.schedule;

import java.nio.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.server.scheduler.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  IntervalInverseTo extends ScheduleInterval implements IntervalInverseTo_Helper {
	
	public int inverseToDeviceId;
	
	@JsOverlay 
	public static IntervalInverseTo create() {
		return Schedule_HelperInheritace.createIntervalInverseTo();
	}
	
	@JsOverlay 
	public static IntervalInverseTo create(int inverseToDeviceId) {
		IntervalInverseTo intervalInverseTo = create();
		intervalInverseTo.inverseToDeviceId = inverseToDeviceId;
		return intervalInverseTo;
	}

	@Override @GwtIncompatible
	public Interval nextInterval(TaskContext context) {
		// TODO
		return null;
	}

	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { IntervalInverseTo_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public IntervalInverseTo deserialize(int ver, ByteBuffer in, DbContext context) { return IntervalInverseTo_Db.deserializeFields(this, in, context); }
}
