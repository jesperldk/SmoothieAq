package jesperl.dk.smoothieaq.shared.model.schedule;

import java.time.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.util.shared.*;

@GwtIncompatible public class  Interval extends Pair<Instant, Instant> {

	public Interval(Instant start, Instant end) { 
		super(start, end);
	}
	
	public Instant start() { return a; }
	public Instant end() { return b; }

}
