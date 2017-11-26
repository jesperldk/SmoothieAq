package jesperl.dk.smoothieaq.shared.error;

public class Error extends Message {
	public Severity severity;
	public long stamp;
	
	public Error(int errorNo, Severity severity, String defaultMessage, Object... args) {
		super(errorNo,defaultMessage,args);
		this.severity = severity;
		this.stamp = System.currentTimeMillis();
	}
	
	@Override public String toString() { return severity+super.toString(); }
}