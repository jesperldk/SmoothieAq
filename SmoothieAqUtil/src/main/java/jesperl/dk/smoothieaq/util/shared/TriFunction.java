package jesperl.dk.smoothieaq.util.shared;

@FunctionalInterface
public interface TriFunction<S,T,U,V> {
	V apply(S s, T t, U u);
}
