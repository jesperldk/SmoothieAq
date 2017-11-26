package jesperl.dk.smoothieaq.util.shared;

public class  Triple<S,T,U> {
	public final S a;
	public final T b;
	public final U c;
	public Triple(S a, T b, U c) { this.a = a; this.b = b; this.c = c; }
	public S getA() { return a; }
	public T getB() { return b; }
	public U getC() { return c; }
	@Override public String toString() { return "("+a+","+b+","+c+")"; }
}
