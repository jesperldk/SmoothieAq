package jesperl.dk.smoothieaq.shared.model.schedule;

import java.nio.*;
import java.time.temporal.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public abstract class  EveryNPoint extends SchedulePoint implements EveryNPoint_Helper {
	
	public boolean relativeToActual;

	@GwtIncompatible protected class  Adjuster {
		final int every;
		final TemporalUnit periodUnit;
		final TemporalAdjuster adjust;
		public Adjuster(int every, TemporalUnit periodUnit, TemporalAdjuster adjust) {
			this.every = every;
			this.periodUnit = periodUnit;
			this.adjust = adjust;
		}
	}
	
	@GwtIncompatible abstract protected Adjuster getAdjuster();
	
	@Override @JsOverlay @GwtIncompatible public abstract EveryNPoint deserialize(int ver, ByteBuffer in, DbContext context);
}
