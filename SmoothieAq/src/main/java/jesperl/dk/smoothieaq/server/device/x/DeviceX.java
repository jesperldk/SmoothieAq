package jesperl.dk.smoothieaq.server.device.x;

import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;

public interface DeviceX extends Idable {
	
	public String getDescription();
	public default DeviceType deviceClasse() { return DeviceType.other; }
	public default DeviceClass deviceType() { return DeviceClass.onoff; }
	public default void validate(State state, Device device) {}
	public default void resetDefaultTasks(State state, Device device) {}
	public default CalibrationInfoX getCalibrationInfo() {return null;}
	default public int daysBetweenCalibration() { return 0; }
	public default MeasurementType measurementType() { return MeasurementType.noMeasure; }
	public Measure currentValue(State state, Device device, DeviceCalibration calibration);
	public Measure currentWatt(State state, Device device, DeviceCalibration calibration);
	public default boolean isOn(State state, Device device) { return true; }
}
