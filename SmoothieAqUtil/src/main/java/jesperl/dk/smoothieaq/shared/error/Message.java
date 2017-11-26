package jesperl.dk.smoothieaq.shared.error;

import java.text.*;

public class Message {
	public int msgNo;
	public String defaultMessage;
	public Object[] args;
	
	public Message(int msgNo, String defaultMessage, Object... args) {
		this.msgNo = msgNo; this.defaultMessage = defaultMessage; this.args = args;
	}
	
	@Override public String toString() { return "("+msgNo+") "+ MessageFormat.format(defaultMessage, args); }
}