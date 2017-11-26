package jesperl.dk.smoothieaq.server.device.x;

import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;

public abstract class  AbstractInstrumentX extends AbstractDeviceX implements InstrumentX {

	public static class  CanMeasureImpl implements InstrumentX.CanMeasure {
		private double precision;
		
		CanMeasureImpl(double precision) {
			this.precision = precision;
		}

		@Override public double precision() {
			return precision;
		}

	}
	
	private CanMeasureImpl canMeasure;
	
	public AbstractInstrumentX(String description, MeasurementType measurementType, double precision) {
		super(description, measurementType, DeviceType.sensor, DeviceClass.sensor);
		canMeasure = new CanMeasureImpl(precision);
	}

	@Override
	public CanMeasure canMeasure() {
		return canMeasure;
	}
	
	@Override
	public Measure measureOrCalculateCurrentValue(State state, Device instance, DeviceCalibration calibration) {
		return measure(state, instance, MeasureType.normal);
	}
	
//	@Override
//	public void resetDefaultTasks(State state, Device component) {
//		super.resetDefaultTasks(state, component);
//		
//		AutoMeasureTask measureTask = new AutoMeasureTask(state.getNextId(), (Instrument)component, new EveryNMinutes(false, 5)); 
//		// TODO save
//		
//		component.addTask(state, measureTask);
//	}

//	public class  CanMeassureImpl implements CanMeasure {
//		MeasurementType canMeassure;
//		List<MeasurementType> willAlsoMeassure = Collections.emptyList();
//		Set<MeasurementType> requiresMeassure = Collections.emptySet();
//		Value precision;
//		int millisMeassurement = 10;
//		int daysBetweenCalibrations = 0;
//		List<CalibrationType> calibrationTypes = new ArrayList<>();
//		CanMeassureImpl() {}
//		CanMeassureImpl(MeasurementType canMeassure, double precision) {
//			this.canMeassure = canMeassure;
//			this.precision = canMeassure.getDefaultUnit().value(precision);
//			Value anyValue = canMeassure.getDefaultUnit().value(CalibrationType.anyValue);
//			CalibrationTypeImpl any2Type = new CalibrationTypeImpl("Any two calibration values","...");
//			any2Type.add(anyValue);
//			any2Type.add(anyValue);
//			calibrationTypes.add(any2Type);
//			CalibrationTypeImpl any3Type = new CalibrationTypeImpl("Any three calibration values","...");
//			any3Type.add(anyValue);
//			any3Type.add(anyValue);
//			any3Type.add(anyValue);
//			calibrationTypes.add(any3Type);
//			CalibrationTypeImpl any1Type = new CalibrationTypeImpl("A single calibration value","...");
//			any1Type.add(anyValue);
//			calibrationTypes.add(any1Type);
//		}
//		@Override public MeasurementType canMeasure() { return canMeassure; }
//		@Override public List<MeasurementType> willAlsoMeasure() { return willAlsoMeassure; }
//		@Override public Set<MeasurementType> requiresMeasure() { return requiresMeassure; }
//		@Override public Value getPrecision() { return precision; }
//		@Override public double getPrecision(Value value) { return canMeassure.convert(getPrecision(), value.getUnit()).getValue(); }
//		@Override public int millisMeasurement(MeasureType meassureType) { return 4; }
//		@Override public int getDaysBetweenCalibrations() { return daysBetweenCalibrations; }
//		@Override public List<CalibrationType> getCalibrationTypes() { return calibrationTypes; }
//	}
//	
//	public class  CalibrationTypeImpl implements CalibrationType {
//		String description;
//		String longDescription;
//		List<List<Value>> calibrationValues = new ArrayList<>();
//		CalibrationTypeImpl() {}
//		CalibrationTypeImpl(String description, String longDescription) {
//			this.description = description;
//			this.longDescription = longDescription;
//		}
//		void add(Value... values) { calibrationValues.add(new ArrayList<>(Arrays.asList(values))); }
//		@Override public String getDescription() { return description; }
//		@Override public String getLongDescription() { return longDescription; }
//		@Override public List<List<Value>> calibrationValues() { return calibrationValues; }
//	}
//	
//	public abstract class  CalibrationImpl implements Calibration {
//		private static final long serialVersionUID = 9001L;
//		private Date calibrationDate = new Date();
//		private boolean finalized = false;
//		@Override public Date getCalibrationDate() { return calibrationDate; }
//		@Override public List<Value> calibrate(Instrument instance, List<Value> calibrationValues) {
//			assert !finalized;
//			return ((AbstractInstrumentType)(instance.getInstrumentType())).meassureForCalibration(this, calibrationValues);
//		}
//		@Override public void finalizeCalibration(Instrument instance) {
//			((InstrumentImpl)instance).setNewCalibration(this);
//			this.finalized = true;
//		}
//		
//	}
//	
//	private List<CanMeasure> canMeassure;
//
//	public AbstractInstrumentType(int id) {
//		super(id);
//	}
//
//	@Override
//	public String getDescription() {
//		return description;
//	}
//	
//	protected void setDescription(String description) {
//		this.description = description;
//	}
//	
//	protected void setCanMeassure(List<CanMeasure> canMeassure) {
//		this.canMeassure = canMeassure;
//	}
//
//	protected void setCanMeassure(CanMeasure... canMeassure) {
//		this.canMeassure = new ArrayList<>(Arrays.asList(canMeassure));
//	}
//	
//	protected void setCanMeassure(MeasurementType canMeassure, double precision) {
//		setCanMeassure(new CanMeassureImpl(canMeassure, precision));
//	}
//
//	@Override
//	public List<CanMeasure> canMeasure() {
//		return canMeassure;
//	}
//	
//	abstract protected List<Value> meassureForCalibration(CalibrationImpl calibration, List<Value> calibrationValues);
}
