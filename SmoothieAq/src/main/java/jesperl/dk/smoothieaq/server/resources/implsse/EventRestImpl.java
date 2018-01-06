package jesperl.dk.smoothieaq.server.resources.implsse;

import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;

import java.util.logging.*;

import jesperl.dk.smoothieaq.server.resources.impl.*;
import jesperl.dk.smoothieaq.shared.model.event.*;
import jesperl.dk.smoothieaq.shared.resources.*;
import jesperl.dk.smoothieaq.util.shared.error.*;
import rx.*;

public class EventRestImpl extends RestImpl implements EventRest {
	private final static Logger log = Logger.getLogger(EventRestImpl.class.getName());

	@Override public Observable<Event> events() { 
		return wires().eventsMux.onBackpressureBuffer(500, ()->error(log, 130101, Severity.info, "Dropping events"), BackpressureOverflow.ON_OVERFLOW_DROP_OLDEST); 
	}

}
