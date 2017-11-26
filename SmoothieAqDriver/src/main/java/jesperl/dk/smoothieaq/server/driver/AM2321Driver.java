package jesperl.dk.smoothieaq.server.driver;

import static jesperl.dk.smoothieaq.server.access.abstracts.DeviceAccessUtils.*;
import static jesperl.dk.smoothieaq.server.util.Utils.*;
import static jesperl.dk.smoothieaq.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.shared.error.Severity.*;
import static jesperl.dk.smoothieaq.shared.util.Objects.*;

import java.util.*;
import java.util.function.*;
import java.util.logging.*;

import jesperl.dk.smoothieaq.server.access.*;
import jesperl.dk.smoothieaq.server.access.abstracts.*;
import jesperl.dk.smoothieaq.server.access.classes.*;
import jesperl.dk.smoothieaq.server.driver.abstracts.*;
import jesperl.dk.smoothieaq.shared.error.*;
import jesperl.dk.smoothieaq.shared.util.*;

/**
 * http://akizukidenshi.com/download/ds/aosong/AM2321_e.pdf
 * 
 * Read the temperature
 *   Send: (SLA+W)+0x03+0x02+0x02
 *   Return: 0x03+0x02+High temperature+low temperature+CRC
 * Read humidity
 *   Send: (SLA+W)+0x03+0x00+0x02
 *   Return: 0x03+0x02+High humidity+Low humidity+CRC
 * Reading Device Information
 *   Send: (SLA+W)+0x03+0x08+0x07
 *   Return: 0x03+0x07+Model(16)+version number(8)+ID(32-bit)+CRC
 */
public abstract class AM2321Driver extends AbstractSensorDriver<AM2321Driver.Storage,ByteDeviceAccess> {
	private final static Logger log = Logger.getLogger(AM2321Driver.class.getName());

	private static final byte fixedI2Caddress = 0x5c;
	private static final byte readRegistersFunctionCode = 0x03;
	@SuppressWarnings("unused")	private static final byte tempRegisterAdr = 0x02;
	private static final byte humidRegisterAdr = 0x00;
	
	public static class Storage extends AbstractSensorDriver.Storage {
		public float temp;
		public float humid;
		{ initCalibration(2); }
	}

	@Override public void init(DeviceAccessContext context, String urlString, float[] calibration) {
		init(context, urlString, ByteDeviceAccess.class, AM2321Driver.class, () -> new Storage(), calibration);
	}
	@Override public Message description() { return msg(20102,"AM2321 low precision humidity (relative humidity in %) and temperature (°C) sensor"); }
	@Override public List<String> getDefaultUrls(DeviceAccessContext context) { return list(DeviceUrl.url(I2cDeviceAccess.bus, "1", ""+fixedI2Caddress)); }

	@Override protected void measureAndStore(Storage s) {
		doGuarded(() -> {
			Pair<Float,Float> p = readRegisters(humidRegisterAdr, 4, r -> new Pair<Float,Float>(floatFromBytes(r,2,1,99),floatFromBytes(r,4,5,70)));
			s.humid = p.a + s.calibration[0];
			s.temp = p.b + s.calibration[1];
			log.fine("measured "+s.humid+"% and "+s.temp+"°C");
		});
	}
	
	protected Float floatFromBytes(byte[] registers, int start, float min, float max) {
		float v = shortFromBytes(registers,start)/10.0f;
		if (v < min || v > max) throw error(log,20101,minor,"Read error");
		return v;
	}
	  
	protected <V> V readRegisters(byte registerAdr, int noBytesFromRegister, Function<byte[],V> map) {
		return funcDeviceAccess(da -> {
			byte[] request = new byte[] {readRegistersFunctionCode,registerAdr,(byte)noBytesFromRegister};
			da.flush();
			try {
				return da.writeThenRead(request, noBytesFromRegister+4, null, 8, 200, map);
			} catch (Exception e) {
				da.reopen();
				reallySleep(1000);
				da.flush();
				return da.writeThenRead(request, noBytesFromRegister+4, null, 20, 200, map);
			}
		});
	}
	
	@Override protected List<String> shiftFields() { return list("humidity","temperature"); }
}
