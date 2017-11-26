package jesperl.dk.smoothieaq.server.resources;

import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import jesperl.dk.smoothieaq.server.device.classes.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.task.*;
import jesperl.dk.smoothieaq.shared.resources.*;
import rx.*;

public class  DeviceRestImpl extends RestImpl implements DeviceRest {
	
	@Override public Single<Device> get(int id) { 
		return Single.just(idev(id).model().getDevice()); 
	}

	@Override public Single<DeviceCompactView> create(Device device) {
		return compactSingle(context().create(device));
	}

	@Override public Single<DeviceCompactView> update(Device device) {
		return compactSingle(idev(device.id).model().replace(state(),device));
	}

	@Override public Single<DeviceCompactView> getStatus(int deviceId) {
		return compactSingle(idev(deviceId));
	}

	@Override public Single<DeviceCompactView> statusChange(int deviceId, DeviceStatusChange statusChange) {
		return compactSingle(idev(deviceId).changeStatus(state(), statusChange));
	}

//	@Override public Single<DeviceCompactView> forceOn(int instanceId, boolean on) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override public Single<DeviceView> getView(int deviceId) {
		return Single.just(view(idev(deviceId)));
	}

	@Override public Observable<DeviceCompactView> devices() {
		return context().devices().map(DeviceRestImpl::compactView);
	}

	@Override public Observable<DriverView> drivers() {
		return context().drivers().map(t -> {
			DriverView driver = new DriverView();
			driver.driverId = t.a.shortValue();
			driver.name = t.c.name();
			driver.description = t.c.description();
			driver.deviceClass = t.b;
			driver.defaultUrls = array(t.c.getDefaultUrls(context().daContext()));
			return driver;
		});
	}

	@Override
	public Single<LegalStatusChanges> getLegalStatusChanges(int instanceId) {
		LegalStatusChanges legalStatusChanges = new LegalStatusChanges();
		legalStatusChanges.legalChanges = idev(instanceId).legalCommands();
		return Single.just(legalStatusChanges);
	}
	
	protected static Single<DeviceCompactView> compactSingle(IDevice wrapper) {
		return Single.just(compactView(wrapper));
	}

	protected static DeviceCompactView compactView(IDevice idev) {
		DeviceCompactView view = new DeviceCompactView();
		Device device = idev.model().getDevice();
		view.deviceId = device.id;
		view.deviceClass = device.deviceType;
		view.deviceType = device.deviceClass;
		view.description = device.description;
		view.name = device.name;
		view.statusType = idev.model().getStatus().statusType;
//		view.on = false;
		return view;
	}

	protected static DeviceView view(IDevice idev) {
		DeviceView view = new DeviceView();
		view.device = idev.model().getDevice();
		view.tasks = (Task[])idev.model().getTasks().toArray();
		view.statusType = idev.model().getStatus().statusType;
//		view.on = false;
		return view;
	}
}
