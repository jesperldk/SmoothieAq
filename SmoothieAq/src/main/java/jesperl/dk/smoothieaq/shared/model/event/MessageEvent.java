package jesperl.dk.smoothieaq.shared.model.event;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.util.shared.error.*;
import jsinterop.annotations.*;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  MessageEvent extends Event implements MessageEvent_Helper { 

	public Message message;

	@GwtIncompatible public static MessageEvent create(Message message) {
		MessageEvent messageEvent = new MessageEvent();
		messageEvent.message = message;
		return messageEvent;
	}

}
