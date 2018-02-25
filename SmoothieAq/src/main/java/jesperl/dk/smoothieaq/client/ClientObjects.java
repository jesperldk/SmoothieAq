package jesperl.dk.smoothieaq.client;

import static jesperl.dk.smoothieaq.util.shared.Objects.*;
import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;

import java.util.*;

import static jesperl.dk.smoothieaq.client.components.GuiUtil.*;

import jesperl.dk.smoothieaq.shared.model.db.*;
import jesperl.dk.smoothieaq.util.shared.*;
import jesperl.dk.smoothieaq.util.shared.error.*;
import rx.functions.*;

public class ClientObjects {

	@SafeVarargs
	static public <K extends Enum<K>,V> Map<K,V> emap(Class<K> cls, Pair<K,V>... ps) {
		Map<K,V> map = new EMap<>(cls);
		forEach(ps, p -> map.put(p.a,p.b));
		return map;
	}
	
	@SafeVarargs
	static public <K extends Enum<K>,V> Func1<K,V> func(Class<K> cls, Pair<K,V>... ps) {
		Map<K, V> emap = emap(cls, ps);
		return key -> emap.get(key);
	}
	
	@SuppressWarnings("serial")
	private static class EMap<K extends Enum<K>,V> extends HashMap<K, V> implements Map<K,V> {
		private Class<K> cls;

		public EMap(Class<K> cls) { this.cls = cls; }

		@Override public boolean containsKey(Object key) { return super.containsKey(fixup(key)); }
		@Override public V get(Object key) { return super.get(fixup(key)); }
		@Override public V put(K key, V value) { return super.put(fixup(key),value); }
		@Override public V remove(Object key) { return super.remove(fixup(key)); }

		private K fixup(Object key) { return EnumField.fixup(cls, key); }
	}

	public static rx.Observer<Object> robs() { return new SingleResouceObserver<>(); }
	public static class SingleResouceObserver<T> implements rx.Observer<T> {
		@Override public void onCompleted() {}
		@Override public void onNext(T t) {}
		@Override public void onError(Throwable e) { resouceError(e); }
	}
	
	public static void resouceError(Throwable e) { wToastError(error(e, 200101, Severity.medium, "Error from Resource call", e.toString())); }
}
