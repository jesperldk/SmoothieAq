package jesperl.dk.smoothieaq.client.context;

import static jesperl.dk.smoothieaq.shared.model.device.DeviceStreamUtil.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.util.*;
import java.util.function.*;

import jesperl.dk.smoothieaq.client.*;
import jesperl.dk.smoothieaq.shared.resources.DeviceRest.*;
import rx.*;
import rx.Observable;
import rx.subjects.*;

public class CDevices {
	
	private Map<Integer, CDevice> idToDevice = new HashMap<>();
	private Map<Integer, String> idToName = new HashMap<>();
	
	private Completable ready = Resources.device.devices()
			.doOnNext(d -> deviceChanged(d))
			.count().cache().toCompletable();

	private final Subject<CDevice, CDevice> devicesSubject = PublishSubject.create();
	
	public Single<CDevice> device(int id) {
		return ready.toSingle(() -> idToDevice.get(id));
	}
	
	public Observable<CDevice> devices() {
		return ready.andThen(Observable.from(idToDevice.values())).concatWith(devicesSubject);
	}
	
	public void deviceChanged(DeviceCompactView compactView) {
		CDevice cDevice = idToDevice.get(compactView.deviceId);
		if (cDevice == null) {
			cDevice = new CDevice(compactView.deviceId);
			idToDevice.put(compactView.deviceId, cDevice);
			devicesSubject.onNext(cDevice);
		}
		idToName.put(compactView.deviceId, compactView.name);
		cDevice.compactViewSubject.onNext(compactView);
	}
	
	public Function<Float,String> formatter(int deviceId, short streamId) {
		return nnv(funcNotNull(idToDevice.get(deviceId), cd -> cd.formatter(streamId)), v -> strv(v));
	}
	public String name(int deviceId) {
		if (deviceId == 0) return "";
		return idToName.get(deviceId); 
	} 
	public String name(int deviceId, short streamId) { 
		if (deviceId == 0) return "";
		if (streamId == 0) return idToName.get(deviceId);
		return idToName.get(deviceId)+"."+fromId.get(streamId).name(); 
	} 
}
