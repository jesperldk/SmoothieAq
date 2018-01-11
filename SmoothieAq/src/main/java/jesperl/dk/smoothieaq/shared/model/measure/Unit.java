package jesperl.dk.smoothieaq.shared.model.measure;

import static java.lang.Math.*;
import static jesperl.dk.smoothieaq.shared.model.measure.MeasurementUtil.*;

//import java.text.*;
import java.util.*;
import java.util.function.*;

import com.google.gwt.i18n.client.*;

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
	yesno(81),
	onoff(83),
	status(83),
	;

	private int id;
	
	private Unit(int id) { this.id = id; }
	
	public int getId() { return id; }
	public String getName() { return unitToInfo.get(this).name; }
	public String getSuffix() { return unitToInfo.get(this).suffix; }
	public Unit getDefaultUnit() { return unitToInfo.get(this).defaultUnit; }
	public List<Unit> getAlternativeUnits() { return unitToInfo.get(this).alternativeUnits; }
	public float convertToDefault(float value) { return unitToInfo.get(this).convertToDefault.apply(value); }
	public float convertFromDefault(float value) { return unitToInfo.get(this).convertFromDefault.apply(value); }

	@Override public String toString() { return getSuffix(); }
	
	public static NumberFormat dec0 = NumberFormat.getFormat("#,##0");
	public static NumberFormat dec1 = NumberFormat.getFormat("#,##0.0");
	public static NumberFormat dec2 = NumberFormat.getFormat("#,##0.00");
	public static NumberFormat dec3 = NumberFormat.getFormat("#,##0.000");
	public Function<Float,String> formatter() { return formatter((float) pow(10.,-unitToInfo.get(this).defaultDecimals)); }
	public Function<Float,String> formatter(float repeatabilityLevel) {
		if (this == onoff) return v -> (v== null || v < -999) ? "-" : (v < 0.001) ? "off" : "on"; 
		if (this == status) return v -> (v== null || v < -999) ? "-" : (v < 0.001) ? "off" : (v < 1.001) ? "on" : "blink"+round(v);
		NumberFormat fmt =
			(repeatabilityLevel < 0.01) ? dec3 :
			(repeatabilityLevel < 0.1) ? dec2 :
			(repeatabilityLevel < 1) ? dec1 :
			dec0;
		String suf = (getSuffix().length() > 0) ? " "+getSuffix() : "";
		return v -> (v== null || v < -999) ? "-" : fmt.format(v)+suf;
	}

	public String toString(float value) { return formatter().apply(value); }

}
