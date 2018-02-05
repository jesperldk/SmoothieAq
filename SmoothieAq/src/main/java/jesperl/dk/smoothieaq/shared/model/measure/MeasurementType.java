package jesperl.dk.smoothieaq.shared.model.measure;

import static jesperl.dk.smoothieaq.shared.model.measure.MeasurementUtil.*;

public enum MeasurementType {
	
	noMeasure(0),
	otherMeasure(1),
	acidity(2), 
	energyConsumption(3), 
	temperature(4),
	volume(5),
	weight(6),
	change(7),
	humidity(8),
	onoff(9),
	status(10),
	alarm(11),
	;
	
	private int id;
	private MeasurementType(int id) { this.id = id; }
	public int getId() { return id; }
	
	public String getName() { return measurementTypeToInfo.get(this).name; }
	public Unit getUnit() { return measurementTypeToInfo.get(this).unit; }

}
