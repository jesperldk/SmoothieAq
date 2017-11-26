package jesperl.dk.smoothieaq.shared.model.db;

import java.nio.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jsinterop.annotations.*;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public abstract class  DbObject {

	@JsOverlay public abstract DbObject copy();
	@JsOverlay @GwtIncompatible public abstract void serialize(ByteBuffer out, DbContext context);
	@JsOverlay @GwtIncompatible public abstract DbObject deserialize(int ver, ByteBuffer in, DbContext context);
}
