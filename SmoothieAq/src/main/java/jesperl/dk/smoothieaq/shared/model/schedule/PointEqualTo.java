package jesperl.dk.smoothieaq.shared.model.schedule;

import java.nio.*;
import java.time.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.server.scheduler.*;
import jesperl.dk.smoothieaq.server.task.classes.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jesperl.dk.smoothieaq.util.shared.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  PointEqualTo extends SchedulePoint {
	
	public int equalToDeviceId;

	@JsOverlay 
	public static PointEqualTo create(int deviceId) {
		PointEqualTo pointEqualTo = Schedule_Helper.createPointEqualTo();
		pointEqualTo.equalToDeviceId = deviceId;
		return pointEqualTo;
	}

	@Override @GwtIncompatible 
	public Instant next(TaskContext context) {
		ITask task = context.getDeviceTask(equalToDeviceId);
		if (task == null) return null; // TASK no longer exists - TODO
		TaskContext taskContext = context.taskContext(task.getId());
		do {
			Pair<? extends Schedule, Interval> next = task.model().getTask().nextForTask(taskContext);
			if (next == null) return null; // hmm??? - TODO
			Instant start = next.b.start();
		if (start.isAfter(context.instant())) return start;
			taskContext = taskContext.offsetTo(next.b);
		} while (true);
	}

	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { PointEqualTo_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public PointEqualTo deserialize(int ver, ByteBuffer in, DbContext context) { return PointEqualTo_Db.deserializeFields(this, in, context); }
}
