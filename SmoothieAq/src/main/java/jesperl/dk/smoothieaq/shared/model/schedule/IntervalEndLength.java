package jesperl.dk.smoothieaq.shared.model.schedule;

import java.nio.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.server.scheduler.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object") 
public class  IntervalEndLength extends ScheduleInterval implements IntervalEndLength_Helper {
	
	public SchedulePoint end;
	public ScheduleTime length;
	
	@JsOverlay 
	public static IntervalEndLength create() {
		return Schedule_HelperInheritace.createIntervalEndLength();
	}
	
	@JsOverlay 
	public static IntervalEndLength create(SchedulePoint end, ScheduleTime length) {
		IntervalEndLength intervalEndLength = create();
		intervalEndLength.end = end;
		intervalEndLength.length = length;
		return intervalEndLength;
	}

	@Override @GwtIncompatible
	public Interval nextInterval(TaskContext context) {
		// TODO
		return null;
	}

	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { IntervalEndLength_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public IntervalEndLength deserialize(int ver, ByteBuffer in, DbContext context) { return IntervalEndLength_Db.deserializeFields(this, in, context); }
}
