package jesperl.dk.smoothieaq.shared.model.schedule;

import static java.time.temporal.ChronoUnit.*;

import java.nio.*;
import java.time.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  EveryNHours extends EveryDuration {
	
	public short hours;
	public short atMinute;
	
	@GwtIncompatible private Adjuster adjuster;
	
	@JsOverlay 
	public static EveryNHours create(boolean relativeToActual, int hours, int atMinute) {
		EveryNHours everyNHours = Schedule_Helper.createEveryNHours();
		everyNHours.relativeToActual = relativeToActual;
		everyNHours.hours = (short) hours;
		everyNHours.atMinute = (short) atMinute;
		return everyNHours;
	}

	@Override @GwtIncompatible
	protected Adjuster getAdjuster() {
		if (adjuster == null)
			adjuster = new Adjuster(hours, HOURS, (t) -> ((Instant)t).plusSeconds(atMinute*60));
		return adjuster;
	}
	
	@Override @JsOverlay public EveryNHours copy() { return EveryNHours_Db.copy(new EveryNHours(),this); }
	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { EveryNHours_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public EveryNHours deserialize(int ver, ByteBuffer in, DbContext context) { return EveryNHours_Db.deserializeFields(this, in, context); }
}
