package jesperl.dk.smoothieaq.util.shared.error;

import static jesperl.dk.smoothieaq.util.shared.Objects.*;
import static jesperl.dk.smoothieaq.util.shared.error.Severity.*;

import java.util.*;
import java.util.function.*;
import java.util.logging.*;

public abstract class  Errors {
	private final static Logger log = Logger.getLogger(Errors.class .getName());

	public interface Action {
		void doit();
		default Action noException() { return () -> doNoException(()->doit()); }
		default Action guarded() { return () -> doGuarded(()->doit()); }
		default Doit asDoit() { return () -> doit(); }
		default Action and(Action andAction) { return () -> { doit(); andAction.doit(); }; }
		default Action and(Doit andDoit) { return and(andDoit.asAction()); }
		default Consumer<Object> consumer() { return o -> doit(); }
	}
	
	public interface Doit { 
		void doit() throws Exception; 
		default Doit noException() { return () -> doNoException(this); }
		default Doit guarded() { return () -> doGuarded(this); }
		default Action asAction() { return () -> doGuarded(this); }
		default Doit and(Action andAction) { return () -> { doit(); andAction.doit(); }; }
		default Doit and(Doit andDoit) { return () -> { doit(); andDoit.doit(); }; }
		default Consumer<Object> consumer() { return o -> doGuarded(this); }
	}
	
	
	public interface Supplyit<T> { T doit() throws Exception; }
	public interface ErrorHandler extends Function<ErrorException,ErrorException> {}
	public interface ErrorXHandler extends Function<Throwable,ErrorException> {}
	public static void doNoException(Doit doit) { try { doit.doit(); } catch (Exception e) {} }
	public static void doGuarded(Doit doit) { doGuardedX(doit, e -> error(e)); }
	public static void doGuarded(ErrorHandler errorHandler, Doit doit) { doGuarded(doit, errorHandler); }
	public static void doGuarded(Doit doit, ErrorHandler errorHandler) { doGuardedX(errorHandler, doit, e-> error(e)); }
	public static void doGuarded(ErrorHandler errorHandler, Doit doit, ErrorXHandler errorXHandler) { doGuardedX(errorHandler, doit, errorXHandler); }
	public static void doGuardedX(Doit doit, ErrorXHandler errorXHandler) { 
		try { doit.doit(); } catch (Throwable e) { 
			if (e instanceof ErrorException) throw (ErrorException)e;
			ErrorException ee = errorXHandler.apply(e);
			if (ee != null) throw ee;
		} 
	}
	public static void doGuardedX(ErrorHandler errorHandler, Doit doit, ErrorXHandler errorXHandler) { 
		try { doit.doit(); } catch (Throwable e) { 
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
		try { return doit.doit(); } catch (Throwable e) { 
			if (e instanceof ErrorException) throw (ErrorException)e;
			ErrorException ee = errorXHandler.apply(e);
			if (ee != null) throw ee;
			return null;
		} 
	}
	public static <T> T funcGuardedX(ErrorHandler errorHandler, Supplyit<T> doit, ErrorXHandler errorXHandler) { 
		try { return doit.doit(); } catch (Throwable e) { 
			ErrorException ee = e instanceof ErrorException ? (ErrorException)e : errorXHandler.apply(e); 
			if (ee != null)	ee = errorHandler.apply(ee);
			if (ee != null) throw ee;
			return null;
		} 
	}
	
	public static Message msg(int msgNo, String defaultMessage, Object... args) { return Message.create(msgNo, defaultMessage, args); }
	public static List<Message> msgs(Message... messages) { return list(messages); }
	public static List<Message> msgs(int msgNo, String defaultMessage, Object... args) { return msgs(Message.create(msgNo, defaultMessage, args)); }
	
	public static ErrorException error(Logger log, int errorNo, Severity severity, String defaultMessage, Object... args) {
		return error(log, null, errorNo, severity, defaultMessage, args);
	}
	public static ErrorException error(Logger log, Throwable e, int errorNo, Severity severity, String defaultMessage, Object... args) {
		Error error = Error.create(errorNo, severity, defaultMessage, args);
		if (log != null) { switch (severity) {
			case fatal: if (e == null)  log.severe(error.toString()); else log.log(Level.SEVERE, error.toString(), e); break; 
			case major: if (e == null)  log.warning(error.toString()); else log.log(Level.WARNING, error.toString(), e); break; 
			case medium: if (e == null)  log.info(error.toString()); else log.log(Level.INFO, error.toString(), e);  break; 
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
	public static ErrorException error(Logger log, Throwable e, Severity severity) {
		if (e instanceof ErrorException) return (ErrorException)e;
		return error(log, e, 1, severity, "Unexpected exception: {0}", e.getMessage());
	}
	public static ErrorException error(int errorNo, Severity severity, String defaultMessage, Object... args) {
		return error(log, null, errorNo, severity, defaultMessage, args);
	}
	public static ErrorException error(Throwable e, int errorNo, Severity severity, String defaultMessage, Object... args) {
		return error(log, e, errorNo, severity, defaultMessage, args);
	}
	public static ErrorException error(Throwable e) {
		return error(e,major);
	}
	public static ErrorException error(Throwable e, Severity severity) {
		return error(log, e, severity);
	}

}
