package jesperl.dk.smoothieaq.server.streamexpr.node;

import java.util.*;

import jesperl.dk.smoothieaq.server.device.*;
import rx.*;
import rx.Observable;

public class  StreamConst extends StreamNode {
	public float value;
	public String token;
	
	@Override public Observable<Float> wire(DeviceContext context, List<Subscription> subscriptions) {
		return Observable.just(value);
	}

	@Override public String toString() { return Float.toString(value); }
	@Override public String toSaveable() { return token; }
	@Override public String toShowable() { return token; }
}
