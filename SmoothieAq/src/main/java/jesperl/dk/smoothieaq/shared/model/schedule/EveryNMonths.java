package jesperl.dk.smoothieaq.shared.model.schedule;

import java.nio.*;
import java.time.temporal.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  EveryNMonths extends EveryPeriod implements EveryNMonths_Helper {
	
	public int months;
	public ScheduleDayInMonth dayInMonth;
	public int atSpecifiDay;
	public boolean[] atWeekDays;
	
	@GwtIncompatible private Adjuster adjuster;
	
	@JsOverlay 
	public static EveryNMonths create() {
		return Schedule_HelperInheritace.createEveryNMonths();
	}

	@JsOverlay 
	public static EveryNMonths create(boolean relativeToActual, int months, ScheduleDayInMonth dayInMonth, int atSpecifiDay, boolean[] atWeekDays) {
		assert dayInMonth != null ^ atSpecifiDay != 0 ^ atWeekDays != null;
		EveryNMonths everyNMonths = create();
		everyNMonths.relativeToActual = relativeToActual;
		everyNMonths.months = months;
		everyNMonths.dayInMonth = dayInMonth;
		everyNMonths.atSpecifiDay = atSpecifiDay;
		everyNMonths.atWeekDays = atWeekDays;
		return everyNMonths;
	}

	@Override @GwtIncompatible
	protected Adjuster getAdjuster() {
		if (adjuster == null)
			adjuster = new Adjuster(months,ChronoUnit.MONTHS,null); // TODO
		return adjuster;
	}

	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { EveryNMonths_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public EveryNMonths deserialize(int ver, ByteBuffer in, DbContext context) { return EveryNMonths_Db.deserializeFields(this, in, context); }
}
