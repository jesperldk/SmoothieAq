package jesperl.dk.smoothieaq.client.context;

import static jesperl.dk.smoothieaq.client.context.CContext.*;

import jesperl.dk.smoothieaq.client.*;
import jesperl.dk.smoothieaq.client.timeseries.*;
import jesperl.dk.smoothieaq.shared.model.event.*;

public class TsMeasurements extends TsCache<TsMeasurement> {

	public TsMeasurements() {
		super(
			ctx.cWires.observable(".ME").map(e -> new TsMeasurement((ME)e)), 
			(f,n,o) -> Resources.measure.measuresFrom(f, n, o, new int[0]).map(e -> new TsMeasurement((ME)e)), 
			(n) -> new TsMeasurement[n]);
	}

}
