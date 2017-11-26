package jesperl.dk.smoothieaq.server.driver;

import static javax.xml.bind.DatatypeConverter.*;
import static jesperl.dk.smoothieaq.server.access.abstracts.DeviceAccessUtils.*;
import static jesperl.dk.smoothieaq.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.shared.error.Severity.*;

import java.io.*;

import jesperl.dk.smoothieaq.server.access.classes.*;

/**
  * http://www.analog.com/media/en/technical-documentation/data-sheets/AD7792_7793.pdf
 */
public class AD7793AdcUtil {
	
	/*AD7793 Registers*/
	public static final byte regComm		= 0; /* Communications Register(WO, 8-bit) */
	public static final byte regStat		= 0; /* Status Register	    (RO, 8-bit) */
	public static final byte regMode		= 1; /* Mode Register	     	(RW, 16-bit */
	public static final byte regConf		= 2; /* Configuration Register (RW, 16-bit)*/
	public static final byte regData		= 3; /* Data Register	     	(RO, 16-/24-bit) */
	public static final byte regId	    	= 4; /* ID Register	     	(RO, 8-bit) */
	public static final byte regIo	    	= 5; /* IO Register	     	(RO, 8-bit) */
	public static final byte regOffset  	= 6; /* Offset Register	    (RW, 24-bit */
	public static final byte regFullscale	= 7; /* Full-Scale Register	(RW, 24-bit */

	/* Communications Register Bit Designations (AD7793_REG_COMM) */
	public static final byte commWen	= (byte) (1 << 7); 		/* Write Enable */
	public static final byte commWrite	= (0 << 6); 			/* Write Operation */
	public static final byte commRead   = (1 << 6); 			/* Read Operation */
	public static final byte commAddr(byte x)	{ return (byte) (((x) & 0x7) << 3); }	/* Register Address */
	public static final byte commCread	= (1 << 2); 			/* Continuous Read of Data 	private DeviceAccess deviceAccess */

	private AD7793AdcUtil() {}
	
	public static void init7793(ByteDeviceAccess da) {
		flush7793(da);
		int id = getRegister1Byte(da,regId) & 0x0f;
		if (id != 0x0b) throw error(20200,major,"Got wrong ID >{0}< from AD7793",id);
	}
	
	public static void dumpRegisters(ByteDeviceAccess da) {
		dumpRegisters(da, System.out);
	}

	protected static void dumpRegisters(ByteDeviceAccess da, PrintStream out) {
		out.println("stat: 0x"+printHexBinary(getRegisterRaw(da, regStat, 1)));
		out.println("mode: 0x"+printHexBinary(getRegisterRaw(da, regMode, 2)));
		out.println("conf: 0x"+printHexBinary(getRegisterRaw(da, regConf, 2)));
		out.println("id: 0x"+printHexBinary(getRegisterRaw(da, regId, 1)));
		out.println("io: 0x"+printHexBinary(getRegisterRaw(da, regIo, 1)));
	}
	
	public static byte getRegister1Byte(ByteDeviceAccess da, byte regAddress) { return byteFromBytes(getRegisterRaw(da, regAddress, 1),1); }
	public static int getRegister2Byte(ByteDeviceAccess da, byte regAddress) { return shortFromBytes(getRegisterRaw(da, regAddress, 2),1); }
	public static int getRegister3Byte(ByteDeviceAccess da, byte regAddress) { return intFrom3Bytes(getRegisterRaw(da, regAddress, 3),1); }
	  
	public static byte[] getRegisterRaw(ByteDeviceAccess da, byte regAddress, int size) {
		assert size <= 3;
		byte request[] = { (byte)(commRead | commAddr(regAddress)), 0x00, 0x00, 0x00 };
		return da.writeThenRead(request, 0, size+1, size, null);
	}
	
	public static void setRegister1Byte(ByteDeviceAccess da, byte regAddress, int value) { setRegisterRaw(da, regAddress, bytesFromByte((byte)value), 1); }
	public static void setRegister2Byte(ByteDeviceAccess da, byte regAddress, int value) { setRegisterRaw(da, regAddress, bytesFromShort((short)value), 2); }
	public static void setRegister3Byte(ByteDeviceAccess da, byte regAddress, int value) { setRegisterRaw(da, regAddress, bytes3FromInt(value), 3); }
	
	public static void setRegisterRaw(ByteDeviceAccess da, byte regAddress, byte[] regValue, int size) {
		assert size <= 3;
		byte request[] = { (byte)(commWrite | commAddr(regAddress)), 0x00, 0x00, 0x00 };
		for (int i = 0; i < size; i++) request[i+1] = regValue[i];
		da.writeThenRead(request, 0, size+1, 0, null);
	}

	public static void flush7793(ByteDeviceAccess da) { da.writeThenRead(new byte[] {(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff}, 0, 4, 0, null); }
}
