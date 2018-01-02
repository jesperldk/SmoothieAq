package jesperl.dk.smoothieaq.util.shared;

import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.util.function.*;

public class  Pair<S,T> {
	public final S a;
	public final T b;
	public Pair(S a, T b) { this.a = a; this.b = b; }
	public S getA() { return a; }
	public T getB() { return b; }
	public <U> U apply( BiFunction<S, T, U> func) { return func.apply(a, b); }
	public void with( BiConsumer<S, T> consumer) { consumer.accept(a, b); }
	public void with( Consumer<S> consumerA, Consumer<T> consumerB ) { consumerA.accept(a); consumerB.accept(b); }
	public <U,V> Pair<U,V> map( Function<S,U> funcA, Function<T,V> funcB) { return pair(funcA.apply(a), funcB.apply(b)); }
	@Override public String toString() { return "("+a+","+b+")"; }
}
