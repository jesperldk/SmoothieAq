package jesperl.dk.smoothieaq.shared.model.schedule;

import static java.time.temporal.ChronoUnit.*;

import java.nio.*;
import java.time.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  EveryNDays extends EveryPeriod {
	
	public short days;
	public ScheduleTime atTime;
	
	@GwtIncompatible private transient Adjuster adjuster;
	
	@JsOverlay 
	public static EveryNDays create(boolean relativeToActual, int days, ScheduleTime atTime) {
		EveryNDays everyNDays = Schedule_Helper.createEveryNDays();
		everyNDays.relativeToActual = relativeToActual;
		everyNDays.days = (short) days;
		everyNDays.atTime = atTime;
		return everyNDays;
	}

	@Override @GwtIncompatible
	protected Adjuster getAdjuster() {
		if (adjuster == null)
			adjuster = new Adjuster(days, DAYS, t -> ((LocalDateTime)t).with(atTime.asTime()));
		return adjuster;
	}
	
	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { EveryNDays_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public EveryNDays deserialize(int ver, ByteBuffer in, DbContext context) { return EveryNDays_Db.deserializeFields(this, in, context); }
}
