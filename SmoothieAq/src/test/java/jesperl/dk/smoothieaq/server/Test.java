package jesperl.dk.smoothieaq.server;

import java.time.*;

import jesperl.dk.smoothieaq.server.state.*;

abstract public class  Test {
	
	public static State state = State.state();
	public static NowWithOffset now = state.now;
	
	public static Instant i(int y, int M, int d, int h, int m) { return LocalDateTime.of(y, M, d, h, m).atZone(ZoneId.systemDefault()).toInstant(); }
	public static Instant i(int M, int d, int h, int m) { return i(2017,M,d,h,m); }
	public static Instant i(int y, int M, int d) { return i(y,M,d,0,0); }
	public static Instant i(int M, int d) { return i(2017,M,d,0,0); }
	
	public static String p(Instant i) { return i.atZone(ZoneId.systemDefault()).toString(); }
	
	public static void print(Object o) { System.out.print(o); }
	public static void println(Object o) { System.out.println(o); }
}