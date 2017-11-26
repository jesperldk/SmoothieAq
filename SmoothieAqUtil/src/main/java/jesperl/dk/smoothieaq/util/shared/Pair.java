package jesperl.dk.smoothieaq.util.shared;

public class  Pair<S,T> {
	public final S a;
	public final T b;
	public Pair(S a, T b) { this.a = a; this.b = b; }
	public S getA() { return a; }
	public T getB() { return b; }
	@Override public String toString() { return "("+a+","+b+")"; }
}
