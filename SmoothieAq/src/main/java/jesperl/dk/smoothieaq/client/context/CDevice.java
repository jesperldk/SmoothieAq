package jesperl.dk.smoothieaq.client.context;

import static jesperl.dk.smoothieaq.client.context.CContext.*;

import java.util.*;

import jesperl.dk.smoothieaq.client.timeseries.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.event.*;
import jesperl.dk.smoothieaq.shared.resources.DeviceRest.*;
import rx.Observable;
import rx.subjects.*;

public class CDevice {
	
	public final short id;
	private DeviceCompactView currentCompactView;
	
	/*friend*/ Subject<DeviceCompactView, DeviceCompactView> compactViewSubject = PublishSubject.create();
	public Observable<DeviceCompactView> compactView = compactViewSubject.replay(1).autoConnect();
	/* friend*/ Map<Short, Observable<TsMeasurement>> streams = new HashMap<>();

	/*friend*/ CDevice(short id) { 
		this.id = id;
		compactView.subscribe(cv -> currentCompactView = cv); // also gets it running hot...
	}
	
	public DeviceCompactView getCurrentCompactView() { return currentCompactView; }
	
	// TODO stream() and streamAlsoPaused()
	public Observable<TsMeasurement> stream(DeviceStream deviceStream) { 
		short streamId = (short)deviceStream.getId();
		Observable<TsMeasurement> stream = streams.get(streamId);
		if (stream == null) {
			Subject<TsMeasurement, TsMeasurement> newStream = PublishSubject.create();
			if (DeviceStreamUtil.toType.get(deviceStream) != DeviceStreamType.eventStream ) { // TODO && liveStream
				Observable<ME> currentValue = Observable.empty(); // TODO
				stream = currentValue.map(TsMeasurement::new).concatWith(newStream).replay(1).autoConnect();
				stream.subscribe(); // gets it running hot...
			} else {
				stream = newStream;
			}
			ctx.cWires.subscribeMeasurement(id, streamId, newStream );
			streams.put(streamId, stream);
		}
		return stream;
	}
	
}
