package jesperl.dk.smoothieaq.shared.model.schedule;

import java.nio.*;
import java.time.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.server.scheduler.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  IntervalStartLength extends ScheduleInterval {
	
	public SchedulePoint start;
	public ScheduleTime length;
	
	@JsOverlay 
	public static IntervalStartLength create(SchedulePoint start, ScheduleTime length) {
		IntervalStartLength intervalStartLength = Schedule_Helper.createIntervalStartLength();
		intervalStartLength.start = start;
		intervalStartLength.length = length;
		return intervalStartLength;
	}

	@Override @GwtIncompatible
	public Interval nextInterval(TaskContext context) {
		context = context.offsetTo(context.instant().minusSeconds(length.asTime().toSecondOfDay()-1));
		Instant next = start.next(context);
		return new Interval(next, next.plusSeconds(length.asTime().toSecondOfDay()));
	}

	@Override @JsOverlay public IntervalStartLength copy() { return IntervalStartLength_Db.copy(new IntervalStartLength(),this); }
	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { IntervalStartLength_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public IntervalStartLength deserialize(int ver, ByteBuffer in, DbContext context) { return IntervalStartLength_Db.deserializeFields(this, in, context); }
}
