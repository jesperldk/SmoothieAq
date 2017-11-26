package jesperl.dk.smoothieaq.server.util;

public class Utils {

	public static void reallySleep(long millis) {
		long end = System.currentTimeMillis()+millis;
		do {
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				// ignore
			}
		} while (System.currentTimeMillis() < end);
	}
	
}
