package jesperl.dk.smoothieaq.client.context;

import static jesperl.dk.smoothieaq.client.context.CContext.*;
import static jesperl.dk.smoothieaq.shared.model.device.DeviceStream.*;
import static jesperl.dk.smoothieaq.shared.model.device.DeviceStreamType.*;
import static jesperl.dk.smoothieaq.shared.model.device.DeviceUtil.*;
import static jesperl.dk.smoothieaq.shared.model.measure.MeasurementType.*;
import static jesperl.dk.smoothieaq.shared.model.measure.MeasurementUtil.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import jesperl.dk.smoothieaq.client.*;
import jesperl.dk.smoothieaq.client.timeseries.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;
import jesperl.dk.smoothieaq.shared.resources.DeviceRest.*;
import jesperl.dk.smoothieaq.util.shared.*;
import rx.*;
import rx.Observable;
import rx.subjects.*;

public class CDevice {
	
	public final int id;
	private DeviceCompactView currentCompactView;
	
	/*friend*/ Subject<DeviceCompactView, DeviceCompactView> compactViewSubject = PublishSubject.create();
	public final Observable<Pair<CDevice,DeviceCompactView>> compactView = compactViewSubject.map(cv -> pair(this,cv)).replay(1).autoConnect();
	public final Single<Device> device;
	public final Single<DeviceView> deviceView;
	
	private Map<Short, Pair<MeasurementType,Observable<TsMeasurement>>> streams = new HashMap<>();
	private Map<Short, Function<Float,String>> formatters = new HashMap<>();

	/*friend*/ CDevice(int id) { 
		this.id = id;
		compactView.map(Pair::getB).doOnNext(cv -> {
			cv.deviceClass 		= EnumField.fixup(DeviceClass.class, cv.deviceClass);
			cv.deviceType 		= EnumField.fixup(DeviceType.class, cv.deviceType);
			cv.statusType		= EnumField.fixup(DeviceStatusType.class, cv.statusType);
			cv.measurementType 	= EnumField.fixup(MeasurementType.class, cv.measurementType);
			currentCompactView = cv;
		}).first().subscribe(cv -> { // also gets it running hot...
			setupStream(cv);
		});
		device = Resources.device.get(id);
		deviceView = Resources.device.getView(id);
	}

	protected void setupStream(DeviceCompactView cv) {
		for (DeviceStream devStream: toClientStreams.get(cv.deviceClass)) {
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
			streams.put(streamId, pair(getMeasurementType(streamId), stream));
			formatters.put(streamId, getFormatter(streamId));
		}
		
		short shadowSourceId = (short)toPauseShadowStream.get(currentCompactView.deviceClass).getId();
		Pair<MeasurementType, Observable<TsMeasurement>> pair = streams.get(shadowSourceId);
		Subject<TsMeasurement, TsMeasurement> newStream = PublishSubject.create();
		short streamId = (short) DeviceStream.pauseX.getId();
		ctx.cWires.subscribeMeasurement(id, streamId, newStream );
		Observable<TsMeasurement> stream = Observable.merge(pair.b,newStream).share();
		stream.subscribe(); // gets it running hot...
		streams.put(streamId, pair(pair.a, stream));
		formatters.put(streamId, getFormatter(shadowSourceId));
	}
	
	private Function<Float,String> getFormatter(short streamId) {
		DeviceStream stream = DeviceStreamUtil.get(streamId);
		MeasurementType measurementType = DeviceStreamUtil.toMesurementType.get(stream);
		if (measurementType != otherMeasure || currentCompactView.measurementType == null) {
			MeasurementTypeInfo measurementTypeInfo = measurementTypeToInfo.get(measurementType);
			return measurementTypeInfo.unit.formatter();
		}
		MeasurementTypeInfo measurementTypeInfo = measurementTypeToInfo.get(currentCompactView.measurementType);
		Function<Float, String> formatter = measurementTypeInfo.unit.formatter(currentCompactView.repeatabilityLevel);
		return formatter;
	}
	
	private MeasurementType getMeasurementType(short streamId) {
		MeasurementType measurementType = DeviceStreamUtil.toMesurementType.get(DeviceStreamUtil.get(streamId));
		return (measurementType == otherMeasure) ? currentCompactView.measurementType : measurementType;
	}
	
	public DeviceCompactView getCurrentCompactView() { return currentCompactView; }
	
	public MeasurementType measurementType(short streamId) { return streams.get(streamId).a; }
	public Function<Float,String> formatter(short streamId) { return formatters.get(streamId); }
	public List<Pair<Short,MeasurementType>> streams() { return streams.entrySet().stream().map(e -> p(e.getKey(), e.getValue().a)).collect(Collectors.toList()); }
	public Observable<TsMeasurement> stream(short streamId) { return streams.get(streamId).b; } 
	public Observable<TsMeasurement> streamAlsoPaused(short streamId) {
		return (toPauseShadowStream.get(currentCompactView.deviceClass).getId() == streamId) ? streams.get((short)pauseX.getId()).b : streams.get(streamId).b; 
	}
	public Observable<TsMeasurement> stream() { return streams.get((short)toDefaultStream.get(currentCompactView.deviceClass).getId()).b; } 
	public Function<Float,String> formatter() { return formatters.get((short)toDefaultStream.get(currentCompactView.deviceClass).getId()); }
	
}
