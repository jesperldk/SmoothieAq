package jesperl.dk.smoothieaq.client.context;

import static jesperl.dk.smoothieaq.client.context.CContext.*;
import static jesperl.dk.smoothieaq.shared.model.device.DeviceStream.*;
import static jesperl.dk.smoothieaq.shared.model.device.DeviceStreamType.*;
import static jesperl.dk.smoothieaq.shared.model.device.DeviceUtil.*;
import static jesperl.dk.smoothieaq.shared.model.measure.MeasurementType.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.util.*;
import java.util.stream.*;

import jesperl.dk.smoothieaq.client.timeseries.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;
import jesperl.dk.smoothieaq.shared.resources.DeviceRest.*;
import jesperl.dk.smoothieaq.util.shared.*;
import rx.Observable;
import rx.subjects.*;

public class CDevice {
	
	public final short id;
	private DeviceCompactView currentCompactView;
	
	/*friend*/ Subject<DeviceCompactView, DeviceCompactView> compactViewSubject = PublishSubject.create();
	public Observable<DeviceCompactView> compactView = compactViewSubject.replay(1).autoConnect();
	/* friend*/ Map<Short, Pair<MeasurementType,Observable<TsMeasurement>>> streams = new HashMap<>();

	/*friend*/ CDevice(short id) { 
		this.id = id;
		compactView.doOnNext(cv -> {
			cv.deviceClass 		= EnumField.fixup(DeviceClass.class, cv.deviceClass);
			cv.deviceType 		= EnumField.fixup(DeviceType.class, cv.deviceType);
			cv.statusType		= EnumField.fixup(DeviceStatusType.class, cv.statusType);
			cv.measurementType 	= EnumField.fixup(MeasurementType.class, cv.measurementType);
			currentCompactView = cv;
		}).first().subscribe(cv -> { // also gets it running hot...
			
			for (DeviceStream devStream: toClientStreams.get(currentCompactView.deviceClass)) {
				short streamId = (short) devStream.getId();
				Observable<TsMeasurement> stream;
				Subject<TsMeasurement, TsMeasurement> newStream = PublishSubject.create();
				if (DeviceStreamUtil.toType.get(devStream) == continousStream ) {
					stream = Observable.just(new TsMeasurement(id, streamId, cv.currentValue)).concatWith(newStream).replay(1).autoConnect();
					stream.subscribe(); // gets it running hot...
				} else {
					stream = newStream;
				}
				ctx.cWires.subscribeMeasurement(id, streamId, newStream );
				streams.put(streamId, pair(measurementType(streamId), stream));
			}
			
			Pair<MeasurementType, Observable<TsMeasurement>> pair = streams.get((short)toPauseShadowStream.get(currentCompactView.deviceClass).getId());
			Subject<TsMeasurement, TsMeasurement> newStream = PublishSubject.create();
			short streamId = (short) DeviceStream.pauseX.getId();
			ctx.cWires.subscribeMeasurement(id, streamId, newStream );
			Observable<TsMeasurement> stream = Observable.merge(pair.b,newStream).share();
			stream.subscribe(); // gets it running hot...
			streams.put(streamId, pair(pair.a, stream));
		});
	}
	
	public MeasurementType measurementType(short streamId) {
		MeasurementType measurementType = DeviceStreamUtil.toMesurementType.get(DeviceStreamUtil.get(streamId));
		return (measurementType == otherMeasure) ? currentCompactView.measurementType : measurementType;
	}
	
	public DeviceCompactView getCurrentCompactView() { return currentCompactView; }
	
	// TODO stream() and streamAlsoPaused()
	public List<Pair<Short,MeasurementType>> streams() { return streams.entrySet().stream().map(e -> p(e.getKey(), e.getValue().a)).collect(Collectors.toList()); }
	public Observable<TsMeasurement> stream(short streamId) { return streams.get(streamId).b; } 
	public Observable<TsMeasurement> streamAlsoPaused(short streamId) {
		return (toPauseShadowStream.get(currentCompactView.deviceClass).getId() == streamId) ? streams.get((short)pauseX.getId()).b : streams.get(streamId).b; 
	}
	public Observable<TsMeasurement> stream() { return streams.get((short)toDefaultStream.get(currentCompactView.deviceClass).getId()).b; } 
	
}
