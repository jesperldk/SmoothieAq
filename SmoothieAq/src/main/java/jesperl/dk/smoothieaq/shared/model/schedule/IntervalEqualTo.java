package jesperl.dk.smoothieaq.shared.model.schedule;

import java.nio.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.server.scheduler.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  IntervalEqualTo extends ScheduleInterval {
	
	public int equalToTaskId;
	
	@JsOverlay 
	public static IntervalEqualTo create(int equalToTaskId) {
		IntervalEqualTo intervalEqualTo = Schedule_Helper.createIntervalEqualTo();
		intervalEqualTo.equalToTaskId = equalToTaskId;
		return intervalEqualTo;
	}

	@Override @GwtIncompatible
	public Interval nextInterval(TaskContext context) {
		// TODO
		return null;
	}

	@Override @JsOverlay public IntervalEqualTo copy() { return IntervalEqualTo_Db.copy(new IntervalEqualTo(),this); }
	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { IntervalEqualTo_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public IntervalEqualTo deserialize(int ver, ByteBuffer in, DbContext context) { return IntervalEqualTo_Db.deserializeFields(this, in, context); }
}
