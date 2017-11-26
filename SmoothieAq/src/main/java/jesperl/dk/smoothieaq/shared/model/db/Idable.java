package jesperl.dk.smoothieaq.shared.model.db;

import com.google.gwt.core.shared.*;

import jsinterop.annotations.*;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public interface Idable {
	@GwtIncompatible public void setId(short id);
	@GwtIncompatible public short getId();
}
