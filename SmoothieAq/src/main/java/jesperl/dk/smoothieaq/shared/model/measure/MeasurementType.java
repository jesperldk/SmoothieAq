package jesperl.dk.smoothieaq.shared.model.measure;

import static jesperl.dk.smoothieaq.shared.model.measure.Unit.*;

public enum MeasurementType {
	
	noMeasure(0,"-", noUnit),
	otherMeasure(1,"other",otherUnit),
	acidity(2,"acidity", pH), 
	energyConsumption(3,"energy", watt), 
	temperature(4,"temp", degreeC),
	volume(5,"volume", litre),
	weight(6,"weight", gram),
	change(7,"change", fraction),
	humidity(8,"humidity", fraction),
	onoff(9,"onoff", bool),
	status(10,"status",grade),
	;
	
	private int id;
	private String name;
	private Unit unit;

	private MeasurementType(int id, String name, Unit unit) {
		assert unit.getDefaultUnit() == null;
		this.id = id;
		this.name = name;
		this.unit = unit;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public Unit getUnit() {
		return unit;
	}

}
