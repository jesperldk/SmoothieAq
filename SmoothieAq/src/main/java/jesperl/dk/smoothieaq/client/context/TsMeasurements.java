package jesperl.dk.smoothieaq.client.context;

import static jesperl.dk.smoothieaq.client.context.CContext.*;

import jesperl.dk.smoothieaq.client.timeseries.*;
import jesperl.dk.smoothieaq.shared.model.event.*;
import rx.*;
import rx.functions.*;

public class TsMeasurements implements TsSource<TsMeasurement> {

	@Override public Observable<TsMeasurement> elementsFrom(long from, int preCount, int postCount,	Func1<TsMeasurement, Boolean> predicate) {
		return Observable.range(0, preCount+postCount).map(n -> null);
	}

	@Override public Observable<TsMeasurement> newElements(Func1<TsMeasurement, Boolean> predicate) { 
		return ctx.cWires.observable(".ME").map(e -> new TsMeasurement((ME)e)); 
	}
	
	@Override public Observable<Void> refreshListen() { return Observable.never(); }
	@Override public void release() {}

}
