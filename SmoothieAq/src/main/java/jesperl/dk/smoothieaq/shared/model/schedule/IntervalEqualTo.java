package jesperl.dk.smoothieaq.shared.model.schedule;

import java.nio.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.server.scheduler.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  IntervalEqualTo extends ScheduleInterval implements IntervalEqualTo_Helper {
	
	public int equalToDeviceId;
	
	@JsOverlay 
	public static IntervalEqualTo create() {
		return Schedule_HelperInheritace.createIntervalEqualTo();
	}
	
	@JsOverlay 
	public static IntervalEqualTo create(int equalToDeviceId) {
		IntervalEqualTo intervalEqualTo = create();
		intervalEqualTo.equalToDeviceId = equalToDeviceId;
		return intervalEqualTo;
	}

	@Override @GwtIncompatible
	public Interval nextInterval(TaskContext context) {
		// TODO
		return null;
	}

	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { IntervalEqualTo_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public IntervalEqualTo deserialize(int ver, ByteBuffer in, DbContext context) { return IntervalEqualTo_Db.deserializeFields(this, in, context); }
}
