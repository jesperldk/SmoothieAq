package jesperl.dk.smoothieaq.client.text;

import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.util.*;
import java.util.function.*;

import com.google.gwt.core.client.*;
import com.google.gwt.i18n.client.*;

import jesperl.dk.smoothieaq.shared.model.db.*;

public interface EnumMessages extends ConstantsWithLookup {
	public static EnumMessages enumMsg = GWT.create(EnumMessages.class );

	@DefaultStringValue("category")
	String DeviceCategory();

	@DefaultStringValue("device category")
	String DeviceCategory_Long();
	
	@DefaultStringValue("Device category is a grouping of devices, mainly used when listing and selection devices. The category does not influence the workings of a device.")
	String DeviceCategory_Help();
	
	@DefaultStringMapValue({"primary","primary","secondary","secondary","system","system","external","external","manual","manual"})
	Map<String, String> DeviceCategory_Enum();
	
	@DefaultStringMapValue({"primaryL","primary device","secondaryL","secondary device","systemL","system device","externalL","external device","manualL","manual device"})
	Map<String, String> DeviceCategory_LongEnum();
	
	@DefaultStringMapValue({
		"primaryH","Primary devices should be devices crucial to the workings of your setup.",
		"secondaryH","Secondary devices should be devices that are redundant or in other ways not crucial to the workings of your setup.",
		"systemH","System devices should be devices that are part of the SmoothieAq setup itself.",
		"externalH","External devices should be devices that are not part of the setup, but that you for some reason uses SmoothieAq to control.",
		"manualH","Manual devices should be \"real\" devices, they are just used to hold manual tasks."})
	Map<String, String> DeviceCategory_HelpEnum();

	static Map<Class<?>, String> _names = new HashMap<>();
	static Map<Class<?>, String> _longNames = new HashMap<>();
	static Map<Class<?>, String> _helps = new HashMap<>();
	static Map<Enum<?>,String> _valueNames = new HashMap<>();
	static Map<Enum<?>,String> _valueLongNames = new HashMap<>();
	static Map<Enum<?>,String> _valueHelps = new HashMap<>();
	
	default String _lookup(Class<?> enumClass, Map<Class<?>,String> map, String lookupSuffix,Supplier<String> defaultValue) {
		return _put(map, enumClass, nnv(_lookup(enumClass.getSimpleName()+lookupSuffix), () -> defaultValue.get()));
	}
	default String _lookup(String key) { try { return getString(key); } catch (Exception e) { return null; } }
	default String _lookup(Enum<?> enumValue, Map<Enum<?>,String> map, String lookupSuffix, Supplier<String> defaultValue) {
		return _put(map, enumValue, nnv(_lookup(enumValue.getClass().getSimpleName()+lookupSuffix,enumValue.toString()+suffix2(lookupSuffix)), () -> defaultValue.get()));
	}
	default String suffix2(String suffix1) { return (suffix1.length() == 0) ? "" : suffix1.substring(1, 2); }
	default String _lookup(String key, String key2) { try { return getMap(key).get(key2); } catch (Exception e) { return null; } }
	static <K,V> V _put(Map<K,V> map, K k, V v) { map.put(k, v); return v; } 
	
	default String name(Class<?> enumClass) { return nnv(_names.get(enumClass), () ->  _lookup(enumClass, _names, "", () -> enumClass.getSimpleName())); }
	default String longName(Class<?> enumClass) { return nnv(_longNames.get(enumClass), () -> _lookup(enumClass, _longNames, "_Long", () -> name(enumClass))); }
	default String help(Class<?> enumClass) { return nnv(_helps.get(enumClass), () -> _lookup(enumClass, _helps, "_Help", () -> longName(enumClass))); }
	
	default <T extends Enum<T>> String valueName(Class<T> enumClass, T enumValue) { return valueName(EnumField.fixup(enumClass, enumValue)); }
	default String valueName(Field<?> field) { return valueName((Enum<?>)field.get()); }
	default String valueName(Enum<?> enumValue) { return enumValue == null ? "" : nnv(_valueNames.get(enumValue), () ->  _lookup(enumValue, _valueNames, "_Enum", () -> enumValue.toString())); }
				
	default <T extends Enum<T>> String valueLongName(Class<T> enumClass, T enumValue) { return valueLongName(EnumField.fixup(enumClass, enumValue)); }
	default String valueLongName(Field<?> field) { return valueLongName((Enum<?>)field.get()); }
	default String valueLongName(Enum<?> enumValue) { 
		return enumValue == null ? 
				"" : 
					nnv(_valueLongNames.get(enumValue), 
							() ->  
					_lookup(enumValue, _valueLongNames, "_LongEnum", 
							() -> 
					valueName(enumValue))); }
				
	default <T extends Enum<T>> String valueHelp(Class<T> enumClass, T enumValue) { return valueHelp(EnumField.fixup(enumClass, enumValue)); }
	default String valueHelp(Field<?> field) { return valueHelp((Enum<?>)field.get()); }
	default String valueHelp(Enum<?> enumValue) { return enumValue == null ? "" : nnv(_valueHelps.get(enumValue), () ->  _lookup(enumValue, _valueHelps, "_HelpEnum", () -> valueLongName(enumValue))); }
				
}
