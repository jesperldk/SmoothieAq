package jesperl.dk.smoothieaq.client.context;

import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.util.*;

import jesperl.dk.smoothieaq.client.*;
import jesperl.dk.smoothieaq.client.components.GuiUtil.*;
import jesperl.dk.smoothieaq.shared.resources.DeviceRest.*;
import rx.*;
import rx.Observable;

public class CDrivers {
	
	private Map<Short, DriverView> idToDriver = new HashMap<>();
	
	private Completable ready = Resources.device.drivers()
			.doOnNext(d -> idToDriver.put(d.driverId, d))
			.count().cache().toCompletable();

	public Single<DriverView> driver(short id) {
		return ready.toSingle(() -> idToDriver.get(id));
	}
	
	public Observable<DriverView> drivers() {
		return ready.andThen(Observable.from(idToDriver.values()));
	}

//	private Observable<DriverView> drivers;
//	private Map<Short, DriverView> driversById;
//	private WOptions<Short> options;
//
//	public Observable<DriverView> getDrivers() {
//		if (driversById != null) return Observable.from(driversById.values());
//		if (drivers != null) return drivers;
//		Map<Short, DriverView> byId = new HashMap<>();
//		return drivers = Resources.device.drivers()
//					.doOnNext(d -> {
//						byId.put(d.driverId, d);
//					})
//					.doOnCompleted(() -> {
//						driversById = byId;
//					});
//	}
//	
//	public Single<DriverView> getDriver(short id) {
//		if (driversById != null) return Single.just(driversById.get(id));
//		return getDrivers().last().toSingle().map(d -> driversById.get(id));
//	}
	
	public Single<WOptions<Short>> options() {
		return ready.toSingle(() -> new WOptions<Short>(idToDriver.values().stream().map(dr -> pair(new Short(dr.driverId), dr.name.format()))));
//		if (options != null) return Single.just(options);
//		return getDrivers().last().toSingle()
//				.map(d -> options = new WOptions<Short>(driversById.values().stream().map(dr -> pair(new Short(dr.driverId), dr.name.format()))));
	}

}
