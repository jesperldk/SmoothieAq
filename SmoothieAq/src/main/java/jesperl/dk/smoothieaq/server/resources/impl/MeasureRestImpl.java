package jesperl.dk.smoothieaq.server.resources.impl;

import jesperl.dk.smoothieaq.server.resources.config.*;
import jesperl.dk.smoothieaq.shared.model.event.*;
import jesperl.dk.smoothieaq.shared.resources.*;
import rx.*;

public class  MeasureRestImpl extends RestImpl implements MeasureRest {

	@Override public Observable<ME> measuresFrom(long fromNewestNotIncl, int countNewer, int countOlder, int[] deviceIds) {
		return funcGuarded(() -> dbContext().dbMeasure.stream(fromNewestNotIncl, countNewer, countOlder).map(ME::create));
	}


}
