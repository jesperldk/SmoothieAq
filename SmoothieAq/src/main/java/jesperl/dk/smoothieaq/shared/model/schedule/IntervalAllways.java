package jesperl.dk.smoothieaq.shared.model.schedule;

import java.nio.*;
import java.time.temporal.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.server.scheduler.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  IntervalAllways extends ScheduleInterval implements IntervalAllways_Helper {
	
	@JsOverlay 
	public static IntervalAllways create() {
		return Schedule_HelperInheritace.createIntervalAllways();
	}

	@Override @GwtIncompatible
	public Interval nextInterval(TaskContext context) {
		return new Interval(context.instant(), context.instant().plus(999*360, ChronoUnit.DAYS));
	}

	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { IntervalAllways_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public IntervalAllways deserialize(int ver, ByteBuffer in, DbContext context) { return IntervalAllways_Db.deserializeFields(this, in, context); }
}
