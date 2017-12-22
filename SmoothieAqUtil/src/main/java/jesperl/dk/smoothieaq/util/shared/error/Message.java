package jesperl.dk.smoothieaq.util.shared.error;

import com.google.gwt.core.shared.*;

import jsinterop.annotations.*;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  Message {
	public int msgNo;
	public String defaultMessage;
	public Object[] args;
	
	@JsOverlay public static Message create(int msgNo, String defaultMessage, Object... args) { return init(new Message(),msgNo,defaultMessage,args); }
	@JsOverlay protected static <MSG extends Message> MSG init(MSG msg, int msgNo, String defaultMessage, Object... args) {
		msg.msgNo = msgNo; msg.defaultMessage = defaultMessage; msg.args = args;
		return msg;
	}
	
	@Override @GwtIncompatible public String toString() { return format(); } //"("+msgNo+") "+defaultMessage+" - "+list(args).stream().map(Object::toString).collect(Collectors.joining(",")); }

	@JsOverlay public final String format() { return "("+msgNo+") "+ format(defaultMessage, args); }
	
	@JsOverlay public static String format(String pattern, Object... arguments) {
        String msg = pattern;
        if (arguments != null) {
            for (int index = 0; index < arguments.length; index++) {
                msg = msg.replaceAll("\\{" + (index + 1) + "\\}", String.valueOf(arguments[index]));
            }
        }
        return msg;
    }

}