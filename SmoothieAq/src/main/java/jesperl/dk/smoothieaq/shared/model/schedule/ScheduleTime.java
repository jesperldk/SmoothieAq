package jesperl.dk.smoothieaq.shared.model.schedule;

import java.nio.*;
import java.time.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  ScheduleTime extends DbObject implements ScheduleTime_Helper {

	public short hour;
	public short minute;
	
	@GwtIncompatible transient private LocalTime time;
	
	@JsOverlay 
	public static ScheduleTime create(int hour, int minute) {
		ScheduleTime scheduleTime = new ScheduleTime();
		scheduleTime.hour = (short) hour;
		scheduleTime.minute = (short) minute;
		return scheduleTime;
	} 

	@GwtIncompatible
	public LocalTime asTime() {
		if (time == null)
			time = LocalTime.of(hour, minute);
		return time;
	}
	
	@JsOverlay public final ScheduleTime copy() { return ScheduleTime_Db.copy(new ScheduleTime(),this); }
	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { ScheduleTime_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public ScheduleTime deserialize(int ver, ByteBuffer in, DbContext context) { return ScheduleTime_Db.deserializeFields(this, in, context); }
}
