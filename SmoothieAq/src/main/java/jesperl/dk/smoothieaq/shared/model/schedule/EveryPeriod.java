package jesperl.dk.smoothieaq.shared.model.schedule;

import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.nio.*;
import java.time.*;
import java.time.temporal.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.server.scheduler.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public abstract class  EveryPeriod extends EveryNPoint {
	
	@Override @GwtIncompatible
	public Instant next(TaskContext context) {
		Adjuster a = getAdjuster();
		Interval lastIntv = context.last();
		Instant last = lastIntv == null ? null : lastIntv.start();
		Instant created = context.created();
		if (isNull(last))
			return created;
		
		LocalDateTime nextStart;
		LocalDateTime lastDateTime = LocalDateTime.ofInstant(last, ZoneId.systemDefault());
		if (relativeToActual) {
			nextStart = lastDateTime.plus(a.every,a.periodUnit).truncatedTo(a.periodUnit);
		} else {
			LocalDateTime createdDateTime = LocalDateTime.ofInstant(created, ZoneId.systemDefault());
			long amountToAdd = (createdDateTime.until(context.localDateTime(), a.periodUnit)/a.every+1)*a.every;
			nextStart = createdDateTime.plus(amountToAdd, a.periodUnit);
			nextStart = nextStart.truncatedTo(a.periodUnit);
			if (!nextStart.isAfter(lastDateTime)) { // handles startup and just if the scheduler loop is fast
				nextStart = createdDateTime.plus(amountToAdd+a.every, a.periodUnit);
				nextStart = nextStart.truncatedTo(a.periodUnit);
			} if (lastDateTime.until(nextStart, ChronoUnit.SECONDS) > a.periodUnit.getDuration().getSeconds()*a.every*1.1) { // we skipped one, lets catch up a bit
				nextStart = createdDateTime.plus((createdDateTime.until(context.localDateTime(), a.periodUnit)/a.every)*a.every, a.periodUnit);
				nextStart = nextStart.truncatedTo(a.periodUnit);
			}
		}
		if (isNotNull(a.adjust))
			nextStart = nextStart.with(a.adjust);
		return nextStart.atZone(ZoneId.systemDefault()).toInstant();
	}
	
	@Override @JsOverlay public abstract EveryPeriod copy();
	@Override @JsOverlay @GwtIncompatible public abstract EveryPeriod deserialize(int ver, ByteBuffer in, DbContext context);
}
