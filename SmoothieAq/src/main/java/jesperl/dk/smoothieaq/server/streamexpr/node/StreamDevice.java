package jesperl.dk.smoothieaq.server.streamexpr.node;

import java.util.*;

import jesperl.dk.smoothieaq.server.device.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import rx.*;
import rx.Observable;

public class  StreamDevice extends StreamNode {
	public int deviceId;
	public String deviceName;
	public DeviceStream stream;
	public String token;

	@Override public Observable<Float> wire(DeviceContext context, List<Subscription> subscriptions) {
		if (stream == null) return context.getDevice(deviceId).stream();
		else return context.getDevice(deviceId).stream(stream);
	}

	@Override public String toString() { return toSaveable(); }
	@Override public String toSaveable() { return deviceName+":"+deviceId+(stream == null ? "" : "."+stream); }
	@Override public String toShowable() { return deviceName+(stream == null ? "" : "."+stream); }
}
