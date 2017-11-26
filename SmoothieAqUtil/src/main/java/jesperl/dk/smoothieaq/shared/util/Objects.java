package jesperl.dk.smoothieaq.shared.util;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Objects {
	
	public static <T> T nnv(T valueOrNull, T defaultValue) { return notNullValue(valueOrNull, defaultValue); }
	public static <T> T nnv(T valueOrNull, Supplier<T> supplier) { return notNullValue(valueOrNull, supplier); }
	public static <T> T notNullValue(T valueOrNull, T defaultValue) { return isNotNull(valueOrNull) ? valueOrNull : defaultValue; }
	public static <T> T notNullValue(T valueOrNull, Supplier<T> supplier) { return isNotNull(valueOrNull) ? valueOrNull : supplier.get(); }
	
	public static <T> T notNullOp(T valueOrNull, UnaryOperator<T> op) { return isNotNull(valueOrNull) ? op.apply(valueOrNull) : null; }
	
	public static boolean isNull(Object value) { return value == null; }

	public static boolean isNotNull(Object value) { return value != null; }
	
	public static <T> void doNotNull(T valueOrNull, Consumer<T> doit) { if (isNotNull(valueOrNull)) doit.accept(valueOrNull); }

	public static <T,U> U funcNotNull(T valueOrNull, U defaultValue, Function<T,U> func) { return isNotNull(valueOrNull) ? func.apply(valueOrNull) : defaultValue; }
	public static <T,U> U funcOrNull(T valueOrNull, Function<T,U> func) { return isNotNull(valueOrNull) ? func.apply(valueOrNull) : null; }
	public static <T,U> U funcNotNull(T valueOrNull, Supplier<U> nullFunc, Function<T,U> func) { return isNotNull(valueOrNull) ? func.apply(valueOrNull) : nullFunc.get(); }

	static public <S,T> Pair<S,T> pair(S a, T b) { return new Pair<>(a,b); }
	static public <S> Pair<S,Integer> pair(S a, int b) { return new Pair<>(a,b); }
	static public Pair<Integer,Integer> pair(int a, int b) { return new Pair<>(a,b); }
	static public <S> Pair<S,Float> pair(S a, float b) { return new Pair<>(a,b); }
	static public Pair<Float,Float> pair(float a, float b) { return new Pair<>(a,b); }
	static public <S,T,U> Triple<S,T,U> triple(S a, T b, U c) { return new Triple<>(a,b,c); }
	
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
