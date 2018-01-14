package jesperl.dk.smoothieaq.shared.model.measure;

import static jesperl.dk.smoothieaq.shared.model.measure.MeasurementType.*;
import static jesperl.dk.smoothieaq.shared.model.measure.Unit.*;

import java.util.*;
import java.util.function.*;

public abstract class MeasurementUtil {
	
	public static class UnitInfo {
		public final String name;
		public final String suffix;
		public final int defaultDecimals;
		public final Unit defaultUnit;
		public final List<Unit> alternativeUnits = new ArrayList<>();
		public final Function<Float, Float> convertToDefault;
		public final Function<Float, Float> convertFromDefault;
		UnitInfo(String name, String suffix, int defaultDecimals, Unit defaultUnit, Function<Float, Float> convertToDefault, Function<Float, Float> convertFromDefault) {
			this.name = name;
			this.suffix = suffix;
			this.defaultDecimals = defaultDecimals;
			this.defaultUnit = defaultUnit;
			this.convertToDefault = convertToDefault;
			this.convertFromDefault = convertFromDefault;
		}
		UnitInfo(String name, String suffix, int defaultDecimals, Unit defaultUnit, float factorToDefault) {
			this(name, suffix, defaultDecimals, defaultUnit, f -> f*factorToDefault, f -> f/factorToDefault);
		}
		UnitInfo(String name, String suffix, int defaultDecimals) {
			this(name,suffix, defaultDecimals,null,1);
		}
	}
	
	public static Map<Unit, UnitInfo> unitToInfo = new HashMap<>();
	static {
		unitToInfo.put(noUnit,new UnitInfo("-","",0));
		unitToInfo.put(otherUnit,new UnitInfo("other","",2));
		
		unitToInfo.put(pH,new UnitInfo("pH","pH",1));
		
		unitToInfo.put(degreeC,new UnitInfo("°C","°C",1));
		unitToInfo.put(degreeF,new UnitInfo("°F","°F",1,degreeC, f -> (f-32.0f)/1.8f, f -> f * 1.8f + 32.0f));
		unitToInfo.put(degreeK,new UnitInfo("K","K",1,degreeC, f -> f-273.15f, f -> f+273.15f));
		
		unitToInfo.put(watt,new UnitInfo("W","W",0));

		unitToInfo.put(litre,new UnitInfo("L","L",0));
		unitToInfo.put(millilitre,new UnitInfo("mL","mL",0,litre,1000));
		unitToInfo.put(centilitre,new UnitInfo("cL","cL",0,litre,100));
		unitToInfo.put(impGallon,new UnitInfo("imp.gal","imp.gal",1,litre,4.54609f));
		unitToInfo.put(usGallon,new UnitInfo("us.gal","us.gal",1,litre,3.785411784f));

		unitToInfo.put(gram,new UnitInfo("g","g",0));
		unitToInfo.put(kilogram,new UnitInfo("kg","kg",0,gram,1000));
		unitToInfo.put(ounce,new UnitInfo("oz","oz",0,gram,28.349523125f));
		unitToInfo.put(pound,new UnitInfo("lb","lb",1,gram,453.59237f));

		unitToInfo.put(fraction,new UnitInfo("fraction","",2));
		unitToInfo.put(pct,new UnitInfo("pct","%",0,fraction,1f/100f));
		
		unitToInfo.put(bool,new UnitInfo("bool","",0));
		unitToInfo.put(yesno,new UnitInfo("yes/no","",0));
		unitToInfo.put(Unit.onoff,new UnitInfo("on/off","",0));
		unitToInfo.put(Unit.status,new UnitInfo("status","",0));
	}

	public static class MeasurementTypeInfo {
		public final String name;
		public final Unit unit;
		MeasurementTypeInfo(String name, Unit unit) {
			assert unit.getDefaultUnit() == null;
			this.name = name;
			this.unit = unit;
		}
	}
	
	public static Map<MeasurementType, MeasurementTypeInfo> measurementTypeToInfo = new HashMap<>();
	static {
		measurementTypeToInfo.put(noMeasure,new MeasurementTypeInfo("-", noUnit));
		measurementTypeToInfo.put(otherMeasure,new MeasurementTypeInfo("other",otherUnit));
		measurementTypeToInfo.put(acidity,new MeasurementTypeInfo("acidity", pH));
		measurementTypeToInfo.put(energyConsumption,new MeasurementTypeInfo("energy", watt)); 
		measurementTypeToInfo.put(temperature,new MeasurementTypeInfo("temp", degreeC));
		measurementTypeToInfo.put(volume,new MeasurementTypeInfo("volume", litre));
		measurementTypeToInfo.put(weight,new MeasurementTypeInfo("weight", gram));
		measurementTypeToInfo.put(change,new MeasurementTypeInfo("change", fraction));
		measurementTypeToInfo.put(humidity,new MeasurementTypeInfo("humidity", fraction));
		measurementTypeToInfo.put(MeasurementType.onoff,new MeasurementTypeInfo("onoff", Unit.onoff));
		measurementTypeToInfo.put(MeasurementType.status,new MeasurementTypeInfo("status",Unit.status));
	}
}
