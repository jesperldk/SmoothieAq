package jesperl.dk.smoothieaq.util.shared.error;

public class  Message {
	public int msgNo;
	public String defaultMessage;
	public Object[] args;
	
	public Message(int msgNo, String defaultMessage, Object... args) {
		this.msgNo = msgNo; this.defaultMessage = defaultMessage; this.args = args;
	}
	
	@Override public String toString() { return format(); } //"("+msgNo+") "+defaultMessage+" - "+list(args).stream().map(Object::toString).collect(Collectors.joining(",")); }

	public String format() { return "("+msgNo+") "+ format(defaultMessage, args); }
	
	public static String format(String pattern, Object... arguments) {
        String msg = pattern;
        if (arguments != null) {
            for (int index = 0; index < arguments.length; index++) {
                msg = msg.replaceAll("\\{" + (index + 1) + "\\}", String.valueOf(arguments[index]));
            }
        }
        return msg;
    }

}