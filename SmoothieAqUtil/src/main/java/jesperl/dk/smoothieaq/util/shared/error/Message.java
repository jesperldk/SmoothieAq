package jesperl.dk.smoothieaq.util.shared.error;

import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.text.*;
import java.util.stream.*;

import com.google.gwt.core.shared.*;

public class  Message {
	public int msgNo;
	public String defaultMessage;
	public Object[] args;
	
	public Message(int msgNo, String defaultMessage, Object... args) {
		this.msgNo = msgNo; this.defaultMessage = defaultMessage; this.args = args;
	}
	
	@Override public String toString() { return "("+msgNo+") "+defaultMessage+" - "+list(args).stream().map(Object::toString).collect(Collectors.joining(",")); }

	@GwtIncompatible public String format() { return "("+msgNo+") "+ MessageFormat.format(defaultMessage, args); }
}