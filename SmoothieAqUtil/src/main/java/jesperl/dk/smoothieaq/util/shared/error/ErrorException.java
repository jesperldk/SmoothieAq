package jesperl.dk.smoothieaq.util.shared.error;

public class  ErrorException extends RuntimeException {
	private static final long serialVersionUID = -944494660696905005L;

	private Error error;
	
	public ErrorException(Error error) { super(); this.error = error; }
	public ErrorException(Error error, Exception e) { super(e); this.error = error; }

	public Error getError() { return error; }
	@Override public String getMessage() { return error.toString(); }
}
