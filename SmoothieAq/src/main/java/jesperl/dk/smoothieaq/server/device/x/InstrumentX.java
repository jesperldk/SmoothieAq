package jesperl.dk.smoothieaq.server.device.x;

import java.util.*;

import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;

public interface InstrumentX extends DeviceX {
	
	public enum MeasureType {fast, normal, stable}

	public interface CanMeasure {
		default public List<MeasurementType> requiresMeasure() { return Collections.emptyList(); }
		public double precision();
//		default public double precision(Value value) { return precision(); }
		default public int millisMeasurement(MeasureType measureType) { return 300; }
	}
	
	public CanMeasure canMeasure();
	
	public Measure measure(State state, Device instance, MeasureType measureType);
}
