package jesperl.dk.smoothieaq.shared.model.schedule;

import java.nio.*;
import java.time.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.server.scheduler.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  PointAtDayAbsolute extends PointAtDay {
	
	public ScheduleTime atTime;
	public boolean[] weekDays;
	
	@JsOverlay 
	public static PointAtDayAbsolute create(ScheduleTime atTime, boolean[] weekDays) {
		PointAtDayAbsolute pointAtDayAbsolute = Schedule_Helper.createPointAtDayAbsolute();
		pointAtDayAbsolute.atTime = atTime;
		pointAtDayAbsolute.weekDays = weekDays;
		return pointAtDayAbsolute;
	}
 
	@Override @GwtIncompatible 
	public Instant next(TaskContext context) {
		LocalDateTime now = context.localDateTime();
		LocalDateTime next = now.with(atTime.asTime());
		if (!next.isAfter(now)) {
			next = next.plusDays(1); // TODO handle weekDays
		}
		return next.atZone(ZoneId.systemDefault()).toInstant();
	}

	@Override @JsOverlay public PointAtDayAbsolute copy() { return PointAtDayAbsolute_Db.copy(new PointAtDayAbsolute(),this); }
	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { PointAtDayAbsolute_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public PointAtDayAbsolute deserialize(int ver, ByteBuffer in, DbContext context) { return PointAtDayAbsolute_Db.deserializeFields(this, in, context); }
}
