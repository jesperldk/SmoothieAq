package jesperl.dk.smoothieaq.shared.model.schedule;

import java.nio.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public abstract class PointAtDay extends SchedulePoint {

	@Override @JsOverlay public abstract PointAtDay copy();
	@Override @JsOverlay @GwtIncompatible public abstract PointAtDay deserialize(int ver, ByteBuffer in, DbContext context);
}
