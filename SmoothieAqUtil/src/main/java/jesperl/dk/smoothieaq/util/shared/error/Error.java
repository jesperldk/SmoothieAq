package jesperl.dk.smoothieaq.util.shared.error;

import com.google.gwt.core.shared.*;

import jsinterop.annotations.*;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  Error extends Message {
	public Severity severity;
	public long stamp;
	
	@JsOverlay public static Error create(int errorNo, Severity severity, String defaultMessage, Object... args) {
		Error err = init(new Error(), errorNo, defaultMessage, args);
		err.severity = severity;
		err.stamp = System.currentTimeMillis();
		return err;
	}
	
	@Override @GwtIncompatible public String toString() { return severity+super.toString(); }
}