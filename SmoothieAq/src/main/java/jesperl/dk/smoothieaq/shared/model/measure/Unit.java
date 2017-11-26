package jesperl.dk.smoothieaq.shared.model.measure;

import java.text.*;
import java.util.*;
import java.util.function.*;

import com.google.gwt.core.shared.*;

public enum Unit {

	noUnit(10,"-",0),
	otherUnit(11,"other",2),
	
	pH(20,"pH",1),
	
	degreeC(30,"°C",1),
	degreeF(31,"°F",1,degreeC, f -> (f-32.0f)/1.8f, f -> f * 1.8f + 32.0f),
	degreeK(32,"K",1,degreeC, f -> f-273.15f, f -> f+273.15f),
	
	watt(40,"W",0),

	litre(50,"L",0),
	millilitre(51,"mL",0,litre,1000),
	centilitre(52,"cL",0,litre,100),
	impGallon(53,"imp.gal",1,litre,4.54609f),
	usGallon(54,"us.gal",1,litre,3.785411784f),

	gram(60,"g",0),
	kilogram(61,"kg",0,gram,1000),
	ounce(62,"oz",0,gram,28.349523125f),
	pound(63,"lb",1,gram,453.59237f),

	fraction(70,"fraction",2),
	pct(71,"pct",0,fraction,1f/100f),
	
	bool(80,"bool",0),
	grade(82,"grade",0),
	;

	private int id;
	private String name;
	private int defaultDecimals;
	private Unit defaultUnit;
	private List<Unit> alternativeUnits = new ArrayList<>();
	private Function<Float, Float> convertToDefault;
	private Function<Float, Float> convertFromDefault;

	private Unit(int id, String name, int defaultDecimals, Unit defaultUnit, Function<Float, Float> convertToDefault, Function<Float, Float> convertFromDefault) {
		this.id = id;
		this.name = name;
		this.defaultUnit = defaultUnit;
		if (defaultUnit != null)
			defaultUnit.alternativeUnits.add(this);
		this.defaultDecimals = defaultDecimals;
		this.convertFromDefault = convertFromDefault;
		this.convertToDefault = convertToDefault;
	}

	private Unit(int id, String name, int defaultDecimals, Unit defaultUnit, float factorToDefault) {
		this(id, name,defaultDecimals, defaultUnit, f -> f*factorToDefault, f -> f/factorToDefault);
	}

	private Unit(int id, String name, int defaultDecimals) {
		this(id, name,defaultDecimals,null,1);
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public Unit getDefaultUnit() {
		return defaultUnit;
	}
	
	public List<Unit> getAlternativeUnits() {
		return Collections.unmodifiableList(alternativeUnits);
	}

//	public Value value(float value) {
//		return Value.create(value, this);
//	}
//	
//	public Value convertFrom(Value value) {
//		Unit valueUnit = value.unit;
//		Unit valueDefaultUnit = valueUnit.getDefaultUnit();
//		if (this.equals(valueUnit)) {
//			return value;
//		} if (valueDefaultUnit == null) {
//			assert getDefaultUnit().equals(valueUnit);
//			return value(convertFromDefault(value.value));
//		} else if (getDefaultUnit() == null) {
//			assert getDefaultUnit().equals(valueDefaultUnit);
//			return value(valueUnit.convertToDefault(value.value));
//		} else {
//			assert getDefaultUnit().equals(valueDefaultUnit);
//			return value(convertFromDefault(valueUnit.convertToDefault(value.value)));
//		}
//	}
	
	public float convertToDefault(float value) {
		return convertToDefault.apply(value);
	}

	public float convertFromDefault(float value) {
		return convertFromDefault.apply(value);
	}

	@Override
	public String toString() {
		return getName();
	}

	@GwtIncompatible // TODO
	public String toString(float value) {
		return toString(value, defaultDecimals);
	}

	@GwtIncompatible // TODO
	public String toString(float value, int decimals) {
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(decimals);
		nf.setMinimumFractionDigits(decimals);
		return nf.format(value);
	}

	@GwtIncompatible // TODO
	public String toStringWithUnit(float value) {
		return toStringWithUnit(value, defaultDecimals);
	}

	@GwtIncompatible // TODO
	public String toStringWithUnit(float value, int decimals) {
		return toString(value, decimals) + " " + getName();
	}

}
