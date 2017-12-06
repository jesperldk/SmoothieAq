package jesperl.dk.smoothieaq.shared.model.db;

import java.util.function.*;

public class Field<T> {
	private Supplier<T> get;
	private Consumer<T> set;
	String key;
	private Class<T> type;

	public Field(Supplier<T> get, Consumer<T> set, String key, Class<T> type) {
		this.get = get; this.set = set; this.key = key; this.type = type;
	}

	public T get() { return get.get(); }
	public void set(T value) { set.accept(value); }
	public String getKey() { return key; }
	public Class<T> getType() { return type; }
}
