package jesperl.dk.smoothieaq.client.text;

import com.google.gwt.core.client.*;
import com.google.gwt.i18n.client.*;

import jesperl.dk.smoothieaq.util.shared.error.*;

public interface MsgMessages extends ConstantsWithLookup {
	public static MsgMessages msgMsg = GWT.create(MsgMessages.class );

	@DefaultStringValue("Could not create device id={0} - {1}")
	String M100107();

	default String format(Message msg) {
		String pattern;
		try {
			pattern = getString("M"+msg.msgNo);
		} catch (Exception e) {
			pattern = msg.defaultMessage;
		}
		String text = Message.format(pattern, msg.args);
		if (msg.severity != null) text += " ( "+msg.severity+" "+msg.msgNo+" )";
		return text;
	}
}
