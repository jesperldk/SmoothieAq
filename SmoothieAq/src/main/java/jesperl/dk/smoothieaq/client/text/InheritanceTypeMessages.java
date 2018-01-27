package jesperl.dk.smoothieaq.client.text;

import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.util.*;
import java.util.function.*;

import com.google.gwt.core.client.*;
import com.google.gwt.i18n.client.*;

public interface InheritanceTypeMessages extends ConstantsWithLookup {
	public static InheritanceTypeMessages typeMsg = GWT.create(InheritanceTypeMessages.class );

	@DefaultStringMapValue({
		".TaskArg","arguments",
		".DescriptionTaskArg","description",
		".LevelTaskArg","level",
		".MeasurementTaskArg","measurement",
		".ProgramTaskArg","program",
		".ValueTaskArg","Value",

		".EveryNDays","every N days",
		".EveryNHours","every N hours",
		".EveryNMonths","every N months",
		".EveryNWeeks","every N weeks",
		".IntervalAllways","allways",
		".IntervalEndLength","a duration to a point",
		".IntervalEqualTo","same as another device",
		".IntervalInverseTo","opposite another device",
		".IntervalStartEnd","from a point and to a point",
		".IntervalStartLength","from a point and in a duration",
		".PointAtDayAbsolute","spcific point at day",
		".PointEqualTo","point equal to start of another device",
		".PointEqualToEnd","point equal to end of another device",
		".PointNever","never",
		".PointRelative","relative to schedule of another device",
	})
	Map<String, String> typeName();
	
	@DefaultStringMapValue({
		".TaskArg","task arguments",
		".DescriptionTaskArg","task description",
		".LevelTaskArg","target level",
		".MeasurementTaskArg","manual measurement",
		".ProgramTaskArg","level program",
		".ValueTaskArg","Value"})
	Map<String, String> typeLongName();
	
	@DefaultStringMapValue({
		".TaskArg","task arguments",
		".DescriptionTaskArg","task description",
		".LevelTaskArg","target level",
		".MeasurementTaskArg","manual measurement",
		".ProgramTaskArg","level program",
		".ValueTaskArg","Value"})
	Map<String, String> typeHelp();
	
	static Map<String,String> _typeNames = new HashMap<>();
	static Map<String,String> _typeLongNames = new HashMap<>();
	static Map<String,String> _typeHelps = new HashMap<>();
	
	default String _lookup(String type, Map<String,String> map, String lookupMap,Supplier<String> defaultValue) {
		return _put(map, type, nnv(_lookup(lookupMap,type), () -> defaultValue.get()));
	}
	default String _lookup(String lookupMap, String type) { try { return getMap(lookupMap).get(type); } catch (Exception e) { return null; } }
	static <K,V> V _put(Map<K,V> map, K k, V v) { map.put(k, v); return v; } 
	
	default String typeName(Class<?> cls) { return typeName(cls.getSimpleName()); }
	default String typeName(String type) { return type == null ? "" : nnv(_typeNames.get(type), () ->  _lookup(type, _typeNames, "typeName", () -> type)); }
				
	default String typeLongName(Class<?> cls) { return typeLongName(cls.getSimpleName()); }
	default String typeLongName(String type) { return type == null ? "" : nnv(_typeLongNames.get(type), () ->  _lookup(type, _typeLongNames, "typeLongName", () -> typeName(type))); }
				
	default String typeHelp(Class<?> cls) { return typeHelp(cls.getSimpleName()); }
	default String typeHelp(String type) { return type == null ? "" : nnv(_typeHelps.get(type), () ->  _lookup(type, _typeHelps, "typeHelp", () -> typeLongName(type))); }
				
				
}
