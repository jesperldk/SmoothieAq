package jesperl.dk.smoothieaq.client.context;

import static jesperl.dk.smoothieaq.shared.model.task.TaskStatusType.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import jesperl.dk.smoothieaq.shared.model.db.*;
import jesperl.dk.smoothieaq.shared.model.task.*;
import jesperl.dk.smoothieaq.shared.resources.TaskRest.*;
import jesperl.dk.smoothieaq.util.shared.*;
import rx.*;
import rx.subjects.*;

public class CTask {
	
	public final int id;
	private TaskCompactView currentCompactView;
	private TaskScheduleView currentScheduleView;
	
	/*friend*/ Subject<TaskCompactView, TaskCompactView> compactViewSubject = PublishSubject.create();
	public final Observable<Pair<CTask,TaskCompactView>> compactView = compactViewSubject.map(cv -> pair(this,fixup(cv))).replay(1).autoConnect();
	/*friend*/ Subject<TaskScheduleView, TaskScheduleView> scheduleViewSubject = PublishSubject.create();
	public final Observable<TaskScheduleView> scheduleView = scheduleViewSubject.map(sv -> fixup(sv)).replay(1).autoConnect();
	
	/*friend*/ CDevice cDeviceX;
	public final Single<CDevice> cDevice = Single.fromCallable(() -> cDeviceX);

	//	private Map<Short, Pair<MeasurementType,Observable<TsMeasurement>>> streams = new HashMap<>();
//	private Map<Short, Function<Float,String>> formatters = new HashMap<>();

	/*friend*/ CTask(int id) { 
		this.id = id;
		compactView.map(Pair::getB).doOnNext(cv -> {
			currentCompactView = cv;
		}).first().subscribe(cv -> { // also gets it running hot...
//			setupStream(cv);
		});
		scheduleView.doOnNext(sv -> {
			currentScheduleView = sv;
		}).first().subscribe(cv -> { // also gets it running hot...
//			setupStream(cv);
		});
	}

	public boolean isNotDeleted() { return currentCompactView == null || currentCompactView.statusType != deleted; }

	private TaskScheduleView fixup(TaskScheduleView sv) {
		return sv;
	}

	private TaskCompactView fixup(TaskCompactView cv) {
		cv.statusType 		= EnumField.fixup(TaskStatusType.class, cv.statusType);
		cv.task.taskType	= EnumField.fixup(TaskType.class, cv.task.taskType);
		return cv;
	}

//	protected void setupStream(DeviceCompactView cv) {
//		for (DeviceStream devStream: toClientStreams.get(cv.deviceClass)) {
//			short streamId = (short) devStream.getId();
//			Observable<TsMeasurement> stream;
//			Subject<TsMeasurement, TsMeasurement> newStream = PublishSubject.create();
//			if (DeviceStreamUtil.toType.get(devStream) == continousStream ) {
//				stream = Observable.just(new TsMeasurement(id, streamId, cv.currentValue)).concatWith(newStream).replay(1).autoConnect();
//				stream.subscribe(); // gets it running hot...
//			} else {
//				stream = newStream;
//			}
//			ctx.cWires.subscribeMeasurement(id, streamId, newStream );
//			streams.put(streamId, pair(getMeasurementType(streamId), stream));
//			formatters.put(streamId, getFormatter(streamId));
//		}
//		
//		short shadowSourceId = (short)toPauseShadowStream.get(currentCompactView.deviceClass).getId();
//		Pair<MeasurementType, Observable<TsMeasurement>> pair = streams.get(shadowSourceId);
//		Subject<TsMeasurement, TsMeasurement> newStream = PublishSubject.create();
//		short streamId = (short) DeviceStream.pauseX.getId();
//		ctx.cWires.subscribeMeasurement(id, streamId, newStream );
//		Observable<TsMeasurement> stream = Observable.merge(pair.b,newStream).share();
//		stream.subscribe(); // gets it running hot...
//		streams.put(streamId, pair(pair.a, stream));
//		formatters.put(streamId, getFormatter(shadowSourceId));
//	}
//	
//	private Function<Float,String> getFormatter(short streamId) {
//		DeviceStream stream = DeviceStreamUtil.get(streamId);
//		MeasurementType measurementType = DeviceStreamUtil.toMesurementType.get(stream);
//		if (measurementType != otherMeasure || currentCompactView.measurementType == null) {
//			MeasurementTypeInfo measurementTypeInfo = measurementTypeToInfo.get(measurementType);
//			return measurementTypeInfo.unit.formatter();
//		}
//		MeasurementTypeInfo measurementTypeInfo = measurementTypeToInfo.get(currentCompactView.measurementType);
//		Function<Float, String> formatter = measurementTypeInfo.unit.formatter(currentCompactView.repeatabilityLevel);
//		return formatter;
//	}
//	
//	private MeasurementType getMeasurementType(short streamId) {
//		MeasurementType measurementType = DeviceStreamUtil.toMesurementType.get(DeviceStreamUtil.get(streamId));
//		return (measurementType == otherMeasure) ? currentCompactView.measurementType : measurementType;
//	}
//	
	public TaskCompactView getCurrentCompactView() { return currentCompactView; }
	public TaskScheduleView getCurrentScheduleView() { return currentScheduleView; }
//	
//	public MeasurementType measurementType(short streamId) { return streams.get(streamId).a; }
//	public Function<Float,String> formatter(short streamId) { return formatters.get(streamId); }
//	public List<Pair<Short,MeasurementType>> streams() { return streams.entrySet().stream().map(e -> p(e.getKey(), e.getValue().a)).collect(Collectors.toList()); }
//	public Observable<TsMeasurement> stream(short streamId) { return streams.get(streamId).b; } 
//	public Observable<TsMeasurement> streamAlsoPaused(short streamId) {
//		return (toPauseShadowStream.get(currentCompactView.deviceClass).getId() == streamId) ? streams.get((short)pauseX.getId()).b : streams.get(streamId).b; 
//	}
//	public Observable<TsMeasurement> stream() { return streams.get((short)toDefaultStream.get(currentCompactView.deviceClass).getId()).b; } 
//	public Function<Float,String> formatter() { return formatters.get((short)toDefaultStream.get(currentCompactView.deviceClass).getId()); }
	
}
