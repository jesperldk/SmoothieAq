package jesperl.dk.smoothieaq.client.text;

import static jesperl.dk.smoothieaq.client.text.EnumMessages.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.util.*;
import java.util.function.*;

import com.google.gwt.core.client.*;
import com.google.gwt.i18n.client.*;

import jesperl.dk.smoothieaq.shared.model.db.*;

public interface FieldMessages extends ConstantsWithLookup {
	public static FieldMessages fieldMsg = GWT.create(FieldMessages.class );

	@DefaultStringValue("device")
	String Device();

	@DefaultStringValue("device")
	String Device_Long();

	@DefaultStringValue("A device is a physical og virtual device that either controls og monitors parts of your setup.")
	String Device_Help();

	@DefaultStringValue("type")
	String Device_deviceType();

	@DefaultStringValue("device type")
	String Device_deviceType_Long();

	
	@DefaultStringValue("device URL")
	String Device_deviceUrl();

	@DefaultStringValue("device driver URL")
	String Device_deviceUrl_Long();

	@DefaultStringValue("A device URL is an addess to the physical devices on the computer busses on your SmoothieAq controller.")
	String Device_deviceUrl_Help();
	
	static Map<String, String> _names = new HashMap<>();
	static Map<String, String> _longNames = new HashMap<>();
	static Map<String, String> _helps = new HashMap<>();
	
	default String _lookup(Field<?> field, Map<String,String> map, String lookupSuffix, Supplier<String> defaultValue) {
		return _put(map, field.getKey(), nnv(_lookup(field.getKey().replace('.', '_')+lookupSuffix), () -> {
			if (field.isEnum()) return nnv(enumMsg._lookup(field.getType().getSimpleName()+lookupSuffix), defaultValue);
			return defaultValue.get();
		}));
	}
	default String _lookup(String key) { try { return getString(key); } catch (Exception e) { return null; } }
	static <K,V> V _put(Map<K,V> map, K k, V v) { map.put(k, v); return v; } 
	
	default String name(Field<?> field) { return nnv(_names.get(field.getKey()), () ->  _lookup(field, _names, "", () -> field.getKey())); }
	default String longName(Field<?> field) { return nnv(_longNames.get(field.getKey()), () -> _lookup(field, _longNames, "_Long", () -> name(field))); }
	default String help(Field<?> field) { return nnv(_helps.get(field.getKey()), () -> _lookup(field, _helps, "_Help", () -> longName(field))); }
}
