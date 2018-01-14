package jesperl.dk.smoothieaq.shared.model.db;

import com.google.gwt.core.shared.*;

import jsinterop.annotations.*;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public interface Idable {
	@GwtIncompatible public void setId(int id);
	@GwtIncompatible public int getId();
}
