package jesperl.dk.smoothieaq.shared.model.schedule;

import static java.time.temporal.ChronoUnit.*;

import java.nio.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  EveryNMinutes extends EveryDuration {
	
	public short minutes;

	@GwtIncompatible private Adjuster adjuster;
	
	@JsOverlay 
	public static EveryNMinutes create(boolean relativeToActual, int minutes) {
		EveryNMinutes everyNMinutes = Schedule_Helper.createEveryNMinutes();
		everyNMinutes.relativeToActual = relativeToActual;
		everyNMinutes.minutes = (short) minutes;
		return everyNMinutes;
	}

	@Override @GwtIncompatible
	protected Adjuster getAdjuster() {
		if (adjuster == null)
			adjuster = new Adjuster(minutes,MINUTES,null);
		return adjuster;
	}

	@Override @JsOverlay public EveryNMinutes copy() { return EveryNMinutes_Db.copy(new EveryNMinutes(),this); }
	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { EveryNMinutes_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public EveryNMinutes deserialize(int ver, ByteBuffer in, DbContext context) { return EveryNMinutes_Db.deserializeFields(this, in, context); }
}
