package jesperl.dk.smoothieaq.shared.model.db;

import java.util.function.*;

import rx.*;
import rx.subjects.*;

public class Field<T> {
	protected Supplier<T> get;
	protected Consumer<T> set;
	protected final String key;
	protected final Class<T> type;
	protected Subject<T, T> listenerSubject;

	public Field(Supplier<T> get, Consumer<T> set, String key, Class<T> type) {
		this.get = get; this.set = set; this.key = key; this.type = type;
	}

	public T get() { return get.get(); }
	public void set(T value) { 
		set.accept(value); 
		if (listenerSubject != null) 
			listenerSubject.onNext(value); 
	}
	public String getKey() { return key; }
	public Class<T> getType() { return type; }
	public boolean isEnum() { return false; }
	public Observable<T> listen() {
		if (listenerSubject == null) 
			listenerSubject = PublishSubject.create();
		return Observable.just(get()).concatWith(listenerSubject);
	}
}
