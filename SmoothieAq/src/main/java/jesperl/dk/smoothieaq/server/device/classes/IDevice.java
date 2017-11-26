package jesperl.dk.smoothieaq.server.device.classes;

import java.util.*;

import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.server.task.classes.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;
import jesperl.dk.smoothieaq.shared.model.task.*;
import jesperl.dk.smoothieaq.util.shared.*;
import jesperl.dk.smoothieaq.util.shared.error.Error;
import rx.Observable;
import rx.Observer;

public interface IDevice extends Idable {
	
	public interface Model {
		IDevice replace(State state, Device device);
		IDevice set(State state, DeviceCalibration calibration);
		ITask add(State state, Task task);
	
		Device getDevice();
		DeviceCalibration getCalibration();
		List<ITask> getTasks();
		DeviceStatus getStatus();
	}
	
	Model model();
	
	Error inError();
	void clearError();

	boolean isCalibrationNeeded();

	DeviceStatusChange[] legalCommands();
	IDevice changeStatus(State state, DeviceStatusChange change);
	boolean isEnabled();

	void setValue(float value);
	float getValue();
	
	Observer<Float> drain();
//	Observer<Void> pulse();
	Observable<Float> stream();
	Observable<Float> stream(DeviceStream streamId);
	Observable<Pair<DeviceStream,MeasurementType>> streams();
}