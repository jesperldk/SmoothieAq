package jesperl.dk.smoothieaq.shared.model.event;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.shared.model.measure.*;
import jsinterop.annotations.*;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  ME extends Event implements MessageEvent_Helper { 

	public int i;
	public long t;
	public float v;

	@GwtIncompatible public static ME create(Measure measure) {
		ME event = new ME();
		event.i = (short) (measure.deviceId*256+measure.stream.getId());
		event.t = measure.stamp;
		event.v = measure.value;
		return event;
	}

}
