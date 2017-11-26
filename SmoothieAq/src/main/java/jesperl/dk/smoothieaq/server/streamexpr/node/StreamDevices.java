package jesperl.dk.smoothieaq.server.streamexpr.node;

import java.util.*;

import jesperl.dk.smoothieaq.server.device.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;
import rx.*;
import rx.Observable;

public class  StreamDevices extends StreamNode {
	public DeviceType deviceType;
	public MeasurementType measurementType;
	public DeviceStream stream;
	public String token;
	
	@Override public Observable<Float> wire(DeviceContext context, List<Subscription> subscriptions) {
		return null; // TODO
	}

	@Override public String toString() { return toSaveable(); }
	@Override public String toSaveable() { return null; }
	@Override public String toShowable() { return null; }
}
