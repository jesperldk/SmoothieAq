package jesperl.dk.smoothieaq.server.streamexpr.node;

import java.util.*;

import jesperl.dk.smoothieaq.server.device.*;
import rx.*;
import rx.Observable;

public abstract class  StreamNode {

	public abstract Observable<Float> wire(DeviceContext context, List<Subscription> subscriptions);
	
	public abstract String toSaveable();
	public abstract String toShowable();

}
