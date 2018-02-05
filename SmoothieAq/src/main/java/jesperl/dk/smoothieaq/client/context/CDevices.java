package jesperl.dk.smoothieaq.client.context;

import static jesperl.dk.smoothieaq.shared.model.device.DeviceStreamUtil.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.util.*;
import java.util.function.*;

import jesperl.dk.smoothieaq.client.*;
import jesperl.dk.smoothieaq.client.timeseries.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.resources.DeviceRest.*;
import jesperl.dk.smoothieaq.util.shared.*;
import rx.*;
import rx.Observable;
import rx.subjects.*;

public class CDevices {
	
	private Map<Integer, CDevice> idToDevice = new HashMap<>();
	private Map<Integer, String> idToName = new HashMap<>();

	private Set<Integer> errorDeviceIds = new HashSet<>();
	private Set<Integer> alarmDeviceIds = new HashSet<>();
	private Set<Integer> warningDeviceIds = new HashSet<>();

	private Subject<Set<Integer>, Set<Integer>> errorDevicesSubject = PublishSubject.create();
	public final Observable<Set<Integer>> errorDevices = errorDevicesSubject.replay(1).autoConnect();
	private Subject<Pair<Set<Integer>,Set<Integer>>, Pair<Set<Integer>,Set<Integer>>> alarmDevicesSubject = PublishSubject.create();
	public final Observable<Pair<Set<Integer>,Set<Integer>>> alarmDevices = alarmDevicesSubject.replay(1).autoConnect();

	private Completable ready = Resources.device.devices()
			.doOnNext(d -> deviceChanged(d))
			.count().cache().toCompletable();

	private final Subject<CDevice, CDevice> devicesSubject = PublishSubject.create();
	
	public CDevices() {
		errorDevices.subscribe();
		alarmDevices.subscribe();
		errorsOnNext();
		alarmOnNext();
	}

	private void alarmOnNext() { alarmDevicesSubject.onNext(pair(new HashSet<>(alarmDeviceIds),new HashSet<>(warningDeviceIds))); }
	private void errorsOnNext() { errorDevicesSubject.onNext(new HashSet<>(errorDeviceIds)); }
	
	public Single<CDevice> device(int id) {
		return ready.toSingle(() -> idToDevice.get(id));
	}
	
	public Observable<CDevice> devices() {
		return ready.andThen(Observable.from(idToDevice.values())).concatWith(devicesSubject);
	}
	
	public void deviceChanged(DeviceCompactView compactView) {
		int deviceId = compactView.deviceId;
		CDevice cDevice = idToDevice.get(deviceId);
		boolean isNew = cDevice == null;
		if (isNew) {
			cDevice = new CDevice(deviceId);
			idToDevice.put(deviceId, cDevice);
			devicesSubject.onNext(cDevice);
		}
		idToName.put(compactView.deviceId, compactView.name);
		cDevice.compactViewSubject.onNext(compactView);
		if (isNew) cDevice.stream(DeviceStream.alarm).subscribe(a -> updateAlarms(deviceId, a));
		updateErrors(compactView);
	}

	private void updateErrors(DeviceCompactView compactView) {
		if (compactView.error != null && !errorDeviceIds.contains(compactView.deviceId)) {
			errorDeviceIds.add(compactView.deviceId); errorsOnNext();
		} else if (compactView.error == null && errorDeviceIds.contains(compactView.deviceId)) {
			errorDeviceIds.remove(compactView.deviceId); errorsOnNext();
		}
	}

	private void updateAlarms(int deviceId, TsMeasurement a) {
		if (a.value < 0.01f) {
			if (alarmDeviceIds.contains(deviceId)) {
				alarmDeviceIds.remove(deviceId); alarmOnNext();
			} else if (warningDeviceIds.contains(deviceId)) {
				warningDeviceIds.remove(deviceId); alarmOnNext();
			}
		} else if (a.value < 1.01f) {
			if (alarmDeviceIds.contains(deviceId)) {
				alarmDeviceIds.remove(deviceId); alarmOnNext();
			} else if (!warningDeviceIds.contains(deviceId)) {
				warningDeviceIds.add(deviceId); alarmOnNext();
			}
		} else  {
			if (!alarmDeviceIds.contains(deviceId)) {
				alarmDeviceIds.add(deviceId); alarmOnNext();
			} else if (warningDeviceIds.contains(deviceId)) {
				warningDeviceIds.remove(deviceId); alarmOnNext();
			}
		}
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
