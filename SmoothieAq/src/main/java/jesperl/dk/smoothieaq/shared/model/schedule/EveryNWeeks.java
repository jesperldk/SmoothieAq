package jesperl.dk.smoothieaq.shared.model.schedule;

import java.nio.*;
import java.time.temporal.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  EveryNWeeks extends EveryPeriod implements EveryNWeeks_Helper {
	
	public int weeks;
	public boolean[] atWeekDays;
	
	@GwtIncompatible private Adjuster adjuster;

	@JsOverlay 
	public static EveryNWeeks create() {
		return Schedule_HelperInheritace.createEveryNWeeks();
	}

	@JsOverlay 
	public static EveryNWeeks create(boolean relativeToActual, int weeks, boolean[] atWeekDays) {
		EveryNWeeks everyNWeeks = create();
		everyNWeeks.relativeToActual = relativeToActual;
		everyNWeeks.weeks = weeks;
		everyNWeeks.atWeekDays = atWeekDays;
		return everyNWeeks;
	}

	@Override @GwtIncompatible
	protected Adjuster getAdjuster() {
		if (adjuster == null)
			adjuster = new Adjuster(weeks,ChronoUnit.WEEKS,null); // TODO
		return adjuster;
	}

	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { EveryNWeeks_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public EveryNWeeks deserialize(int ver, ByteBuffer in, DbContext context) { return EveryNWeeks_Db.deserializeFields(this, in, context); }
}
