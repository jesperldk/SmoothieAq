package jesperl.dk.smoothieaq.server.resources.impl;

import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import jesperl.dk.smoothieaq.server.device.classes.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.resources.*;
import rx.*;

public class  DeviceRestImpl extends RestImpl implements DeviceRest {
	
	@Override public Single<Device> get(int id) { 
		return funcGuarded(() -> Single.just(idev(id).model().getDevice())); 
	}

	@Override public Single<DeviceCompactView> create(Device device) { 
		return funcGuarded(() -> compactSingle(context().create(device)));
	}

	@Override public Single<DeviceCompactView> update(Device device) {
		return funcGuarded(() -> compactSingle(idev(device.id).model().replace(state(),device)));
	}

	@Override public Single<DeviceCompactView> getStatus(int deviceId) {
		return funcGuarded(() -> compactSingle(idev(deviceId)));
	}

	@Override public Single<DeviceCompactView> statusChange(int deviceId, DeviceStatusChange statusChange) {
		return funcGuarded(() -> compactSingle(idev(deviceId).changeStatus(state(), statusChange)));
	}

	@Override public Single<DeviceView> getView(int deviceId) {
		return funcGuarded(() -> Single.just(DeviceRest.view(idev(deviceId))));
	}

	@Override public Observable<DeviceCompactView> devices() {
		return funcGuarded(() -> context().devices().map(DeviceRest::compactView));
	}

	@Override public Observable<DriverView> drivers() {
		return funcGuarded(() -> context().drivers().map(t -> {
			DriverView driver = new DriverView();
			driver.driverId = t.a.shortValue();
			driver.name = t.c.name();
			driver.description = t.c.description();
			driver.deviceClass = t.b;
			driver.defaultUrls = array(t.c.getDefaultUrls(context().daContext()));
			return driver;
		}));
	}

	@Override public Single<LegalStatusChanges> getLegalStatusChanges(int instanceId) {
		return funcGuarded(() -> {
			LegalStatusChanges legalStatusChanges = new LegalStatusChanges();
			legalStatusChanges.legalChanges = idev(instanceId).legalCommands();
			return Single.just(legalStatusChanges);
		});
	}
	
	protected static Single<DeviceCompactView> compactSingle(IDevice wrapper) {
		return Single.just(DeviceRest.compactView(wrapper));
	}

}
