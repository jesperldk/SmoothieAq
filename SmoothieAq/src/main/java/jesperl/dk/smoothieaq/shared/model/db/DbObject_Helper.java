package jesperl.dk.smoothieaq.shared.model.db;

import java.util.*;

import jsinterop.annotations.*;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public interface DbObject_Helper {
	@JsOverlay default Map<String, Field<?>> $fields() { if (((DbObject)this).$fields == null) ((DbObject)this).$fields = new HashMap<>(); return ((DbObject)this).$fields; }
}
