package jesperl.dk.smoothieaq.shared.model.schedule;

import java.nio.*;
import java.time.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.server.scheduler.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class PointRelative extends PointAtDay {
	
	public int relativeToDeviceId;
	public boolean beginning; // true=beginning, false=end of the next schedule from the relative to task
	public boolean earlier; // true=earlier, false=later than the time from the relative task
	public ScheduleTime shiftTime;
	
	@JsOverlay 
	public static PointRelative create(int relativeToDeviceId, boolean beginning, boolean earlier, ScheduleTime shiftTime) {
		PointRelative pointRelative = Schedule_Helper.createPointRelative();
		pointRelative.relativeToDeviceId = relativeToDeviceId;
		pointRelative.beginning = beginning;
		pointRelative.earlier = earlier;
		pointRelative.shiftTime = shiftTime;
		return pointRelative;
	}
	
	@Override @GwtIncompatible
	public Instant next(TaskContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override @JsOverlay public PointRelative copy() { return PointRelative_Db.copy(new PointRelative(),this); }
	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { PointRelative_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public PointRelative deserialize(int ver, ByteBuffer in, DbContext context) { return PointRelative_Db.deserializeFields(this, in, context); }
}
