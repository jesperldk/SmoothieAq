package jesperl.dk.smoothieaq.server.access.abstracts;

import static jesperl.dk.smoothieaq.util.server.Utils.*;
import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;

import java.util.function.*;

public abstract class  DeviceAccessUtils {
	
	public static <T> T retry(UnsafeSupplier<T> supplier) { return retry(3, 400, supplier, null); }
	public static <T> T retry(int retries, UnsafeSupplier<T> supplier) { return retry(retries, 400, supplier, null); }

	// TODO can RX do this??
	public static <T> T retry(int retries, int milliesAfterFailue, UnsafeSupplier<T> supplier, Class<? extends Exception> onlyRetryOn) { // TODO some logging
		assert retries >= 1 && retries < 40 : "invalid retries";
		assert milliesAfterFailue >= 0 && milliesAfterFailue < 100000 : "invalid milliesAfterFailue";
		Exception lastException = null;
		for (int i = 0; i < retries; i++)
			try {
				return supplier.get();
			} catch (Exception e) {
				if (onlyRetryOn != null && onlyRetryOn.isAssignableFrom(e.getClass())) throw error(e);
				lastException = e;
				reallySleep(milliesAfterFailue);
System.out.print(" s"+milliesAfterFailue+" ");
				if (milliesAfterFailue < 2000) milliesAfterFailue *= 1.5;
			}
		throw error(lastException);
	}
	
	@FunctionalInterface public interface UnsafeSupplier<T> {
		T get() throws Exception;
	}
	
	public static Float averageDropOutlier(Supplier<Float> supplier) { return averageDropOutlier(4, 1000, supplier); }

	public static Float averageDropOutlier(int noTries, int milliesBetween, Supplier<Float> supplier) { // TODO some logging
		assert noTries >= 1 && noTries < 20 : "invalid noTries";
		assert milliesBetween >= 0 && milliesBetween < 100000 : "invalid miilliesBetween";
		if (noTries == 1) return supplier.get();
		float values[] = new float[noTries];
		float sum = 0.0f;
		for (int i = 0; i < noTries; i++) {
			values[i] = supplier.get();
			sum += values[i];
			if (i+1 < noTries)reallySleep(milliesBetween);
		}
		float avg = sum/noTries;
		float maxDist = -1.0f;
		int outlier = -1;
		for (int i = 0; i < noTries; i++)
			if (Math.abs(avg-values[i]) > maxDist) { outlier = i; maxDist = Math.abs(avg-values[i]); }
		sum = 0.0f;
		for (int i = 0; i < noTries; i++)
			if (i != outlier) sum += values[i];
		return sum/(noTries-1);
	}
	
	public static byte byteFromBytes(byte[] bytes) { return bytes[0]; }
	public static byte byteFromBytes(byte[] bytes, int at) { return bytes[at]; }
	public static short shortFromBytes(byte b1, byte b2) { return (short) ((b1 & 0xff) << 8 | b2 & 0xff); }
	public static int intFromBytes(byte b1, byte b2, byte b3, byte b4) { return ((int)shortFromBytes(b1,b2) & 0xffff) << 16 | shortFromBytes(b3, b4) & 0xffff; }
	public static int intFromBytes(byte b1, byte b2, byte b3) { return intFromBytes((byte)0, b1, b2, b3); }
	public static short shortFromBytes(byte[] bytes) { return shortFromBytes(bytes[0], bytes[1]); }
	public static short shortFromBytes(byte[] bytes, int from) { return shortFromBytes(bytes[from], bytes[from+1]); }
	public static int intFromBytes(byte[] bytes) { return intFromBytes(bytes[0], bytes[1], bytes[3], bytes[4]); }
	public static int intFromBytes(byte[] bytes, int from) { return intFromBytes(bytes[from], bytes[from+1], bytes[from+3], bytes[from+4]); }
	public static int intFrom3Bytes(byte[] bytes) { return intFromBytes(bytes[0], bytes[1], bytes[2]); }
	public static int intFrom3Bytes(byte[] bytes, int from) { return intFromBytes(bytes[from], bytes[from+1], bytes[from+2]); }
	
	public static byte[] bytesFromByte(byte b) { return bytesFromByte(b, new byte[1], 0); }
	public static byte[] bytesFromShort(short s) { return bytesFromShort(s, new byte[2], 0); }
	public static byte[] bytesFromInt(int i) { return bytesFromInt(i, new byte[4], 0); }
	public static byte[] bytes3FromInt(int i) { return bytes3FromInt(i, new byte[3], 0); }
	public static byte[] bytesFromByte(byte b, byte[] bytes, int to) { bytes[to] = b; return bytes; }
	public static byte[] bytesFromShort(short s, byte[] bytes, int to) { bytes[to] = (byte)(s >> 8 & 0xff); bytes[to+1] = (byte)(s & 0xff); return bytes; }
	public static byte[] bytesFromInt(int i, byte[] bytes, int to) { bytes[to] = (byte) (i >> 24 & 0xff); bytes[to+1] = (byte) (i >> 16 & 0xff); bytes[to+2] = (byte) (i >> 8 & 0xff); bytes[to+3] = (byte) (i & 0xff); return bytes; }
	public static byte[] bytes3FromInt(int i, byte[] bytes, int to) { bytes[to] = (byte) (i >> 16 & 0xff); bytes[to+1] = (byte) (i >> 8 & 0xff); bytes[to+2] = (byte) (i & 0xff); return bytes; }
}
