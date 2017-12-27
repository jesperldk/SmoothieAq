package jesperl.dk.smoothieaq.shared.model.measure;

import static jesperl.dk.smoothieaq.shared.model.measure.MeasurementUtil.*;

import java.text.*;
import java.util.*;

import com.google.gwt.core.shared.*;

public enum Unit {

	noUnit(10),
	otherUnit(11),
	
	pH(20),
	
	degreeC(30),
	degreeF(31),
	degreeK(32),
	
	watt(40),

	litre(50),
	millilitre(51),
	centilitre(52),
	impGallon(53),
	usGallon(54),

	gram(60),
	kilogram(61),
	ounce(62),
	pound(63),

	fraction(70),
	pct(71),
	
	bool(80),
	grade(82),
	;

	private int id;
	
	private Unit(int id) { this.id = id; }
	
	public int getId() { return id; }
	public String getName() { return unitToInfo.get(this).name; }
	public Unit getDefaultUnit() { return unitToInfo.get(this).defaultUnit; }
	public List<Unit> getAlternativeUnits() { return unitToInfo.get(this).alternativeUnits; }
	public float convertToDefault(float value) { return unitToInfo.get(this).convertToDefault.apply(value); }
	public float convertFromDefault(float value) { return unitToInfo.get(this).convertFromDefault.apply(value); }

	@Override public String toString() { return getName(); }

	@GwtIncompatible // TODO
	public String toString(float value) { return toString(value, unitToInfo.get(this).defaultDecimals); }

	@GwtIncompatible // TODO
	public static String toString(float value, int decimals) {
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(decimals);
		nf.setMinimumFractionDigits(decimals);
		return nf.format(value);
	}

	@GwtIncompatible // TODO
	public String toStringWithUnit(float value) { return toStringWithUnit(value, unitToInfo.get(this).defaultDecimals); }

	@GwtIncompatible // TODO
	public String toStringWithUnit(float value, int decimals) { return toString(value, decimals) + " " + getName(); }

}
