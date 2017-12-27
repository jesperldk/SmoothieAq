package jesperl.dk.smoothieaq.shared.model.db;

import java.util.function.*;

public class Field<T> {
	protected Supplier<T> get;
	protected Consumer<T> set;
	protected String key;
	protected Class<T> type;

	public Field(Supplier<T> get, Consumer<T> set, String key, Class<T> type) {
		this.get = get; this.set = set; this.key = key; this.type = type;
	}

	public T get() { return get.get(); }
	public void set(T value) { set.accept(value); }
	public String getKey() { return key; }
	public Class<T> getType() { return type; }
}
