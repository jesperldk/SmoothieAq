package jesperl.dk.smoothieaq.server.device.x;

import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;

public abstract class  AbstractDeviceX extends IdableType implements DeviceX {
	
	private String description;
	private MeasurementType measurementType = MeasurementType.noMeasure;
	private DeviceType deviceClasse = DeviceType.other;
	private DeviceClass deviceType = DeviceClass.onoff;

	public AbstractDeviceX(String description) {
		this.description = description;
	}

	public AbstractDeviceX(String description, MeasurementType measurementType, DeviceType deviceClasse, DeviceClass deviceType) {
		this(description);
		this.measurementType = measurementType;
		this.deviceClasse = deviceClasse;
		this.deviceType = deviceType;
	}

	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public MeasurementType measurementType() {
		return measurementType;
	}
	
	@Override
	public DeviceType deviceClasse() {
		return deviceClasse;
	}
	
	@Override
	public DeviceClass deviceType() {
		return deviceType;
	}
	
	@Override
	public Measure currentWatt(State state, Device device, DeviceCalibration calibration) {
		return null;
//		return energyConsumption.measurement(state, device.getId(), device.wattAt100pct);
	}
	
	@Override
	public final Measure currentValue(State state, Device device, DeviceCalibration calibration) {
//		if (!isOn(state, device))
//			return measurementType().zero();
		return measureOrCalculateCurrentValue(state, device, calibration);
	}

	public Measure measureOrCalculateCurrentValue(State state, Device device, DeviceCalibration calibration) {
//		return measurementType().zero();
		return null;
	}
}
