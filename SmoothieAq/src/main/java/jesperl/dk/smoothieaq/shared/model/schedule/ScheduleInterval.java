package jesperl.dk.smoothieaq.shared.model.schedule;

import java.nio.*;
import java.time.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.server.scheduler.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public abstract class  ScheduleInterval extends Schedule {

	@Override @GwtIncompatible
	public Instant next(TaskContext context) {
		return nextInterval(context).start();
	}

	@Override @JsOverlay public abstract ScheduleInterval copy();
	@Override @JsOverlay @GwtIncompatible public abstract ScheduleInterval deserialize(int ver, ByteBuffer in, DbContext context);
}
 