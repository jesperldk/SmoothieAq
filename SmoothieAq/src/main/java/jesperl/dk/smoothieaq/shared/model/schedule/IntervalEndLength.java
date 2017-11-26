package jesperl.dk.smoothieaq.shared.model.schedule;

import java.nio.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.server.scheduler.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class IntervalEndLength extends ScheduleInterval {
	
	public SchedulePoint end;
	public ScheduleTime length;
	
	@JsOverlay 
	public static IntervalEndLength create(SchedulePoint end, ScheduleTime length) {
		IntervalEndLength intervalEndLength = Schedule_Helper.createIntervalEndLength();
		intervalEndLength.end = end;
		intervalEndLength.length = length;
		return intervalEndLength;
	}

	@Override @GwtIncompatible
	public Interval nextInterval(TaskContext context) {
		// TODO
		return null;
	}

	@Override @JsOverlay public IntervalEndLength copy() { return IntervalEndLength_Db.copy(new IntervalEndLength(),this); }
	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { IntervalEndLength_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public IntervalEndLength deserialize(int ver, ByteBuffer in, DbContext context) { return IntervalEndLength_Db.deserializeFields(this, in, context); }
}
