package jesperl.dk.smoothieaq.shared.model.event;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.util.shared.error.Error;
import jsinterop.annotations.*;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  ErrorEvent extends Event implements ErrorEvent_Helper { 

	public Error error;

	@GwtIncompatible public static ErrorEvent create(Error error) {
		ErrorEvent errorEvent = new ErrorEvent();
		errorEvent.error = error;
		return errorEvent;
	}

}
