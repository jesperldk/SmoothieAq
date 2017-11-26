package jesperl.dk.smoothieaq.server.access.classes;

import static jesperl.dk.smoothieaq.server.access.abstracts.DeviceAccessUtils.*;

import java.util.function.*;

public interface ByteDeviceAccess extends DeviceAccess {
	
	byte[] writeThenRead(byte[] request, int start, int length, int readLength, Byte until);
	
	public static final Function<byte[],byte[]> nullMap = new Function<byte[], byte[]>() {
		@Override public byte[] apply(byte[] t) { return t; }
	};

	default <T> T writeThenRead(byte[] request, int readLength, Byte until, int retries, int millisBetween, Function<byte[], T> mapAndValidate) {
		if (request == null)
			return writeThenRead(null, 0, 0, readLength, until, retries, millisBetween, mapAndValidate);
		else
			return writeThenRead(request, 0, request.length, readLength, until, retries, millisBetween, mapAndValidate);
	}

	default <T> T writeThenRead(byte[] request, int start, int length, int readLength, Byte until, int retries, int millisBetween, Function<byte[], T> mapAndValidate) {
		synchronized(this) {
			return retry(retries,millisBetween,() -> mapAndValidate.apply(writeThenRead(request, start, length, readLength, until)),null);
		}
	}
	
}
