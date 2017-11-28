package jesperl.dk.smoothieaq.shared.model.schedule;

import java.nio.*;
import java.time.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.server.scheduler.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  PointEqualToEnd extends SchedulePoint {
	
	public int equalToDeviceId;

	@JsOverlay 
	public static PointEqualToEnd create(int deviceId) {
		PointEqualToEnd pointEqualToEnd = Schedule_Helper.createPointEqualToEnd();
		pointEqualToEnd.equalToDeviceId = deviceId;
		return pointEqualToEnd;
	}

	@Override @GwtIncompatible
	public Instant next(TaskContext context) {
		return null; // TODO the code below if from PointEqualTo - this is a litte different...
//		Task task = context.getDeviceTask(equalToDeviceId);
//		if (task == null) return null; // TASK no longer exists - TODO 
//		TaskContext taskContext = context.taskContext(task.id);
//		do {
//			Pair<? extends Schedule, Interval> next = task.nextForTask(taskContext);
//			if (next == null) return null; // hmm??? - TODO
//			Instant start = next.b.end();
//		if (start.isAfter(context.instant())) return start;
//			taskContext = taskContext.offsetTo(next.b);
//		} while (true);
	}

	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { PointEqualToEnd_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public PointEqualToEnd deserialize(int ver, ByteBuffer in, DbContext context) { return PointEqualToEnd_Db.deserializeFields(this, in, context); }
}
