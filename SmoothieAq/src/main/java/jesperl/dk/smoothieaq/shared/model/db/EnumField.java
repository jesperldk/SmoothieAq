package jesperl.dk.smoothieaq.shared.model.db;

import java.util.function.*;

public class EnumField<T extends Enum<T>> extends Field<T> {

	public EnumField(Supplier<T> get, Consumer<T> set, String key, Class<T> type) {
		super(get, set, key, type);
	}

	@Override public T get() { return fixup(type,get.get()); }
	
	public static <T extends Enum<T>> T fixup(Class<T> type, T value) { return (((Object)value) instanceof String) ? value = Enum.valueOf(type, (String)(Object)value) : value; } 
}
