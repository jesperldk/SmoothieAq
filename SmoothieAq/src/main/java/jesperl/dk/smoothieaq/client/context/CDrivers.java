package jesperl.dk.smoothieaq.client.context;

import static jesperl.dk.smoothieaq.client.text.MsgMessages.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.util.*;

import jesperl.dk.smoothieaq.client.*;
import jesperl.dk.smoothieaq.client.components.GuiUtil.*;
import jesperl.dk.smoothieaq.shared.resources.DeviceRest.*;
import rx.*;
import rx.Observable;

public class CDrivers {
	
	private Map<Integer, DriverView> idToDriver = new HashMap<>();
	
	private Completable ready = Resources.device.drivers()
			.doOnNext(d -> idToDriver.put(d.driverId, d))
			.count().cache().toCompletable();

	public Single<DriverView> driver(int id) {
		return ready.toSingle(() -> idToDriver.get(id));
	}
	
	public Observable<DriverView> drivers() {
		return ready.andThen(Observable.from(idToDriver.values()));
	}

	public Single<WOptions<Integer>> options() {
		return ready.toSingle(() -> new WOptions<>(idToDriver.values().stream().map(dr -> triple(new Integer(dr.driverId), msgMsg.format(dr.name), msgMsg.format(dr.description)))));
	}

}
