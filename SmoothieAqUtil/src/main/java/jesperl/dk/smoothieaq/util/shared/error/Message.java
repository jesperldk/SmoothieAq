package jesperl.dk.smoothieaq.util.shared.error;

public class  Message {
	public int msgNo;
	public String defaultMessage;
	public Object[] args;
	
	public Message(int msgNo, String defaultMessage, Object... args) {
		this.msgNo = msgNo; this.defaultMessage = defaultMessage; this.args = args;
	}
	
	@Override public String toString() { return "("+msgNo+") "+defaultMessage+" - "+args; }
}