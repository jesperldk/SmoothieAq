package jesperl.dk.smoothieaq.shared.model.schedule;

import java.nio.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.server.scheduler.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  IntervalInverseToSelf extends ScheduleInterval implements IntervalInverseToSelf_Helper {

	@Override @GwtIncompatible
	public Interval nextInterval(TaskContext context) {
		// TODO
		return null;
	}

	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { IntervalInverseToSelf_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public IntervalInverseToSelf deserialize(int ver, ByteBuffer in, DbContext context) { return IntervalInverseToSelf_Db.deserializeFields(this, in, context); }
}
