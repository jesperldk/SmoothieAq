package jesperl.dk.smoothieaq.client.context;

import java.util.*;

import com.google.gwt.core.client.*;

import jesperl.dk.smoothieaq.client.*;
import jesperl.dk.smoothieaq.shared.resources.DeviceRest.*;
import rx.*;
import rx.Observable;
import rx.subjects.*;

public class CDevices {
	
	private Map<Short, CDevice> idToDevice = new HashMap<>();
	
	private Completable ready = Resources.device.devices()
			.doOnNext(d -> deviceChanged(d))
			.count().cache().toCompletable();

	private final Subject<CDevice, CDevice> devicesSubject = PublishSubject.create();
	
	public Single<CDevice> device(short id) {
		return ready.toSingle(() -> idToDevice.get(id));
	}
	
	public Observable<CDevice> devices() {
		return ready.andThen(Observable.from(idToDevice.values())).concatWith(devicesSubject);
	}
	
	public void deviceChanged(DeviceCompactView compactView) {
		CDevice cDevice = idToDevice.get(compactView.deviceId);
		if (cDevice == null) {
			cDevice = new CDevice(compactView.deviceId);
			idToDevice.put(compactView.deviceId, cDevice); GWT.log("new dev "+cDevice.id);
			devicesSubject.onNext(cDevice);
		}
		cDevice.compactViewSubject.onNext(compactView);
	}
}
