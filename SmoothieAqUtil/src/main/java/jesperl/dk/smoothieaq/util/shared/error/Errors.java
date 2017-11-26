package jesperl.dk.smoothieaq.util.shared.error;

import static jesperl.dk.smoothieaq.util.shared.Objects.*;
import static jesperl.dk.smoothieaq.util.shared.error.Severity.*;

import java.util.*;
import java.util.function.*;
import java.util.logging.*;

public abstract class  Errors {
	private final static Logger log = Logger.getLogger(Errors.class .getName());

	public interface Doit { void doit() throws Exception; }
	public interface Supplyit<T> { T doit() throws Exception; }
	public interface ErrorHandler extends Function<ErrorException,ErrorException> {}
	public interface ErrorXHandler extends Function<Exception,ErrorException> {}
	public static void doNoException(Doit doit) { try { doit.doit(); } catch (Exception e) {} }
	public static void doGuarded(Doit doit) { doGuardedX(doit, e -> error(e)); }
	public static void doGuarded(ErrorHandler errorHandler, Doit doit) { doGuarded(doit, errorHandler); }
	public static void doGuarded(Doit doit, ErrorHandler errorHandler) { doGuardedX(errorHandler, doit, e-> error(e)); }
	public static void doGuarded(ErrorHandler errorHandler, Doit doit, ErrorXHandler errorXHandler) { doGuardedX(errorHandler, doit, errorXHandler); }
	public static void doGuardedX(Doit doit, ErrorXHandler errorXHandler) { 
		try { doit.doit(); } catch (Exception e) { 
			if (e instanceof ErrorException) throw (ErrorException)e;
			ErrorException ee = errorXHandler.apply(e);
			if (ee != null) throw ee;
		} 
	}
	public static void doGuardedX(ErrorHandler errorHandler, Doit doit, ErrorXHandler errorXHandler) { 
		try { doit.doit(); } catch (Exception e) { 
			ErrorException ee = e instanceof ErrorException ? (ErrorException)e : errorXHandler.apply(e); 
			if (ee != null)	ee = errorHandler.apply(ee);
			if (ee != null) throw ee;
		} 
	}
	
	public static <T> T funcNoException(Supplyit<T> doit, T defaultValue) { try { return doit.doit(); } catch (Exception e) { return defaultValue; } }
	public static <T> T funcGuarded(Supplyit<T> doit) { return funcGuardedX(doit, e -> error(e)); }
	public static <T> T funcGuarded(ErrorHandler errorHandler, Supplyit<T> doit) { return funcGuarded(doit, errorHandler); }
	public static <T> T funcGuarded(Supplyit<T> doit, ErrorHandler errorHandler) { return funcGuardedX(errorHandler, doit, e-> error(e)); }
	public static <T> T funcGuarded(ErrorHandler errorHandler, Supplyit<T> doit, ErrorXHandler errorXHandler) { return funcGuardedX(errorHandler, doit, errorXHandler); }
	public static <T> T funcGuardedX(Supplyit<T> doit, ErrorXHandler errorXHandler) { 
		try { return doit.doit(); } catch (Exception e) { 
			if (e instanceof ErrorException) throw (ErrorException)e;
			ErrorException ee = errorXHandler.apply(e);
			if (ee != null) throw ee;
			return null;
		} 
	}
	public static <T> T funcGuardedX(ErrorHandler errorHandler, Supplyit<T> doit, ErrorXHandler errorXHandler) { 
		try { return doit.doit(); } catch (Exception e) { 
			ErrorException ee = e instanceof ErrorException ? (ErrorException)e : errorXHandler.apply(e); 
			if (ee != null)	ee = errorHandler.apply(ee);
			if (ee != null) throw ee;
			return null;
		} 
	}
	
	public static Message msg(int msgNo, String defaultMessage, Object... args) { return new Message(msgNo, defaultMessage, args); }
	public static List<Message> msgs(Message... messages) { return list(messages); }
	public static List<Message> msgs(int msgNo, String defaultMessage, Object... args) { return msgs(new Message(msgNo, defaultMessage, args)); }
	
	public static ErrorException error(Logger log, int errorNo, Severity severity, String defaultMessage, Object... args) {
		return error(log, null, errorNo, severity, defaultMessage, args);
	}
	public static ErrorException error(Logger log, Exception e, int errorNo, Severity severity, String defaultMessage, Object... args) {
		Error error = new Error(errorNo, severity, defaultMessage, args);
		if (log != null) { switch (severity) {
			case fatal: log.severe(error.toString()); break; 
			case major: log.warning(error.toString()); break; 
			case medium: log.info(error.toString()); break; 
			case minor: log.fine(error.toString()); break; 
			case info: log.finer(error.toString()); break;
			default: break;
		} }
		if (e == null) return new ErrorException(error);
		else return new ErrorException(error, e);
	}
	public static ErrorException error(Logger log, Exception e) {
		return error(log, e, major);
	}
	public static ErrorException error(Logger log, Exception e, Severity severity) {
		if (e instanceof ErrorException) return (ErrorException)e;
		return error(log, e, 1, severity, "Unexpected exception: {0}", e.getMessage());
	}
	public static ErrorException error(int errorNo, Severity severity, String defaultMessage, Object... args) {
		return error(log, null, errorNo, severity, defaultMessage, args);
	}
	public static ErrorException error(Exception e, int errorNo, Severity severity, String defaultMessage, Object... args) {
		return error(log, e, errorNo, severity, defaultMessage, args);
	}
	public static ErrorException error(Exception e) {
		return error(e,major);
	}
	public static ErrorException error(Exception e, Severity severity) {
		return error(log, e, severity);
	}

}
