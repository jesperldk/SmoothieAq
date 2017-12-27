package jesperl.dk.smoothieaq.client.context;

import jesperl.dk.smoothieaq.shared.resources.DeviceRest.*;
import rx.*;
import rx.subjects.*;

public class CDevice {
	
	public final short id;
	
	/*friend*/  Subject<DeviceCompactView, DeviceCompactView> compactViewSubject = new SerializedSubject<>(PublishSubject.create());
	
	public  Observable<DeviceCompactView> compactView = compactViewSubject.replay(1).autoConnect();

	/*friend*/ CDevice(short id) { 
		this.id = id;
		compactView.subscribe(); // don't understand why this is needed
	}
	
}
