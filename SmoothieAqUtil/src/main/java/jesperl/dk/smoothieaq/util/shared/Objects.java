package jesperl.dk.smoothieaq.util.shared;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class  Objects {
	
	public static <T> T nnv(T valueOrNull, T defaultValue) { return notNullValue(valueOrNull, defaultValue); }
	public static <T> T nnv(T valueOrNull, Supplier<T> supplier) { return notNullValue(valueOrNull, supplier); }
	public static <T> T notNullValue(T valueOrNull, T defaultValue) { return isNotNull(valueOrNull) ? valueOrNull : defaultValue; }
	public static <T> T notNullValue(T valueOrNull, Supplier<T> supplier) { return isNotNull(valueOrNull) ? valueOrNull : supplier.get(); }
	
	public static <T> T notNullOp(T valueOrNull, UnaryOperator<T> op) { return isNotNull(valueOrNull) ? op.apply(valueOrNull) : null; }
	
	public static boolean isNull(Object value) { return value == null; }

	public static boolean isNotNull(Object value) { return value != null; }
	
	public static <T> void doNotNull(T valueOrNull, Consumer<T> doit) { if (isNotNull(valueOrNull)) doit.accept(valueOrNull); }

	public static <T,U> U funcNotNull(T valueOrNull, Function<T,U> func) { return isNotNull(valueOrNull) ? func.apply(valueOrNull) : null; }
	public static <T,U> U funcNotNull(T valueOrNull, U defaultValue, Function<T,U> func) { return isNotNull(valueOrNull) ? func.apply(valueOrNull) : defaultValue; }
	public static <T,U> U funcOrNull(T valueOrNull, Function<T,U> func) { return isNotNull(valueOrNull) ? func.apply(valueOrNull) : null; }
	public static <T,U> U funcNotNull(T valueOrNull, Supplier<U> nullFunc, Function<T,U> func) { return isNotNull(valueOrNull) ? func.apply(valueOrNull) : nullFunc.get(); }

	public static String notEmptyValue(String valueOrEmpty, String defaultValue) { return isNotEmpty(valueOrEmpty) ? valueOrEmpty : defaultValue; }
	public static String notEmptyValue(String valueOrEmpty, Supplier<String> supplier) { return isNotEmpty(valueOrEmpty) ? valueOrEmpty : supplier.get(); }
	
	public static String notEmptyOp(String valueOrEmpty, UnaryOperator<String> op) { return isNotEmpty(valueOrEmpty) ? op.apply(valueOrEmpty) : null; }
	
	public static boolean isEmpty(String value) { return value == null || value.length() == 0; }

	public static boolean isNotEmpty(String value) { return value != null && value.length() > 0; }
	
	public static void doNotEmpty(String valueOrEmpty, Consumer<String> doit) { if (isNotEmpty(valueOrEmpty)) doit.accept(valueOrEmpty); }

	public static <U> U funcNotEmpty(String valueOrEmpty, Function<String,U> func) { return isNotEmpty(valueOrEmpty) ? func.apply(valueOrEmpty) : null; }
	public static <U> U funcNotEmpty(String valueOrEmpty, U defaultValue, Function<String,U> func) { return isNotEmpty(valueOrEmpty) ? func.apply(valueOrEmpty) : defaultValue; }
	public static <U> U funcOrEmpty(String valueOrEmpty, Function<String,U> func) { return isNotEmpty(valueOrEmpty) ? func.apply(valueOrEmpty) : null; }
	public static <U> U funcNotEmpty(String valueOrEmpty, Supplier<U> nullFunc, Function<String,U> func) { return isNotEmpty(valueOrEmpty) ? func.apply(valueOrEmpty) : nullFunc.get(); }

	static public <S,T> Pair<S,T> pair(S a, T b) { return new Pair<>(a,b); }
	static public <S> Pair<S,Integer> pair(S a, int b) { return new Pair<>(a,b); }
	static public Pair<Integer,Integer> pair(int a, int b) { return new Pair<>(a,b); }
	static public <S> Pair<S,Float> pair(S a, float b) { return new Pair<>(a,b); }
	static public Pair<Float,Float> pair(float a, float b) { return new Pair<>(a,b); }
	static public <S,T,U> Triple<S,T,U> triple(S a, T b, U c) { return new Triple<>(a,b,c); }
	
	static public <S,T,U> Function<Pair<S,T>,U> with(BiFunction<S, T, U> func) { return p -> func.apply(p.a, p.b); }
	static public <S,T> Consumer<Pair<S,T>> with(BiConsumer<S, T> consumer) { return p -> consumer.accept(p.a, p.b); }
	
	@SafeVarargs static public <O> O[] array(O... os) { return os; }
	static public float[] array(float... fs) { return fs; }
	static public int[] array(int... is) { return is; }
	static public boolean[] array(boolean... bs) { return bs; }
	static public String[] array(List<String> sl) { return sl.toArray(new String[sl.size()]); }
	
	@SafeVarargs static public <O> List<O> list(O... os) { return Arrays.asList(os); }
	static public List<Float> listb(float... fs) { ArrayList<Float> fl = new ArrayList<>(fs.length); for (float f: fs) fl.add(f); return fl; }
	static public List<Integer> listb(int... is) { ArrayList<Integer> il = new ArrayList<>(is.length); for (int i: is) il.add(i); return il; }
	static public List<Boolean> listb(boolean... bs) { ArrayList<Boolean> bl = new ArrayList<>(bs.length); for (boolean b: bs) bl.add(b); return bl; }
	
	@SafeVarargs static public <O> Stream<O> stream(O... os) { return Arrays.stream(os); }
	
	@SafeVarargs static public <O> Set<O> set(O... os) { return new HashSet<>(list(os)); }
	static public Set<Integer> set(int... is) { return new HashSet<>(listb(is)); }
	
	@SuppressWarnings("unchecked")
	static public <K,V> Map<K,V> map(Map<K,V> map, Object... os) {
		for (int i = 0; i < os.length; i += 2) {
			K k = (K)os[i];
			V v = (V)os[i+1];
			map.put(k, v);
		}
		return map;
	}
	
	@SafeVarargs public static <O> O[] concat(O[]... oas) {
		int l = 0; for (O[] oa: oas) if (isNotNull(oa)) l += oa.length;
		O[] oar = null;
		int p = 0; 
		for (O[] oa : oas) {
			if (isNull(oa)) continue;
			if (p == 0) oar = Arrays.copyOf(oa, l);
			else System.arraycopy(oa, 0, oar, p, oa.length); 
			p += oa.length; 
		}
		return oar;
	}
	@SafeVarargs public static <O> List<O> concat(List<O>... oss) { List<O> cl = new ArrayList<>(); for (List<O> os: oss) if (os != null) cl.addAll(os); return cl; }
	
	static public <O> void forEach(O[] oa, Consumer<O> consume) { for (int i = 0; i < oa.length; i++) consume.accept(oa[i]); }
	static public <O> void over(O[] oa, IntConsumer consume) { for (int i = 0; i < oa.length; i++) consume.accept(i); }
	static public void forEach(int[] ia, IntConsumer consume) { for (int i = 0; i < ia.length; i++) consume.accept(ia[i]); }
	static public void over(int[] ia, IntConsumer consume) { for (int i = 0; i < ia.length; i++) consume.accept(i); }
	static public void forEach(float[] fa, DoubleConsumer consume) { for (int i = 0; i < fa.length; i++) consume.accept(fa[i]); }
	static public void over(float[] fa, DoubleConsumer consume) { for (int i = 0; i < fa.length; i++) consume.accept(i); }
	
	static public int intv(String ints) { return Integer.parseInt(ints); }
	static public float floatv(String floats) { return Float.parseFloat(floats); }
	static public String strv(int intv) { return Integer.toString(intv); }
	static public String strv(float floatv) { return Float.toString(floatv); }
}
