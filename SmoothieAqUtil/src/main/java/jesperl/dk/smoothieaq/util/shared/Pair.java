package jesperl.dk.smoothieaq.util.shared;

import java.util.function.*;

public class  Pair<S,T> {
	public final S a;
	public final T b;
	public Pair(S a, T b) { this.a = a; this.b = b; }
	public S getA() { return a; }
	public T getB() { return b; }
	public <U> U with( BiFunction<S, T, U> func) { return func.apply(a, b); }
	public void with( BiConsumer<S, T> consumer) { consumer.accept(a, b); }
	@Override public String toString() { return "("+a+","+b+")"; }
}
