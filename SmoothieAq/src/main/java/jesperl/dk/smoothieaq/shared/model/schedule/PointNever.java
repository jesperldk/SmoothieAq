package jesperl.dk.smoothieaq.shared.model.schedule;

import java.nio.*;
import java.time.*;
import java.time.temporal.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.server.scheduler.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  PointNever extends PointAtDay implements PointNever_Helper {
	
	@JsOverlay 
	public static PointNever create() {
		return Schedule_HelperInheritace.createPointNever();
	}
 
	@Override @GwtIncompatible 
	public Instant next(TaskContext context) {
		return context.instant().plus(999, ChronoUnit.YEARS);
	}

	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { PointNever_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public PointNever deserialize(int ver, ByteBuffer in, DbContext context) { return PointNever_Db.deserializeFields(this, in, context); }
}
