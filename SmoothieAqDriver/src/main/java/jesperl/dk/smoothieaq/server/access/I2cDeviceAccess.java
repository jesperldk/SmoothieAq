package jesperl.dk.smoothieaq.server.access;

import static javax.xml.bind.DatatypeConverter.*;
import static jesperl.dk.smoothieaq.server.util.Utils.*;
import static jesperl.dk.smoothieaq.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.shared.error.Severity.*;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import com.pi4j.io.i2c.*;
import com.pi4j.io.i2c.I2CFactory.*;

import jesperl.dk.smoothieaq.server.access.abstracts.*;
import jesperl.dk.smoothieaq.server.access.classes.*;

public class I2cDeviceAccess extends AbstractDeviceAccess implements ByteDeviceAccess {
	private final static Logger log = Logger.getLogger(I2cDeviceAccess.class.getName());
	
	public static String bus = bus(I2cDeviceAccess.class);
	
	public int sleepAfterFlushMillis = 100;

	private int i2cBus;
	private byte i2cAddress;
	private I2CDevice device;
	private byte[] reply = new byte[2048]; 
	
	// URL: i2c://pi-i2c-bus-no/i2c-address
	@Override public ByteDeviceAccess init(DeviceAccessContext context, DeviceUrl url) {
		super.init(context, url);
		doGuardedX(eh,() -> {
			i2cBus = Integer.parseInt(url.busSelector);
			i2cAddress = Byte.parseByte(url.deviceSelector);
		}, e -> error(log,e,10100, major, "Malformed I2C url: {0}",url.urlString));
		return this;
	}
	
	@Override protected void openIt() {
		doGuarded(eh,() -> {try {
			device = I2CFactory.getInstance(i2cBus).getDevice(i2cAddress); 
			log.info("Opened "+getUrl().urlString);
		} catch (IOException | UnsupportedBusNumberException e) { throw error(log ,e, 10101, medium, "Error opening i2c >{0}< - {1}", getUrl().urlString, e.getMessage()); }});
		super.openIt();
	}
	
	public byte getI2cAddress() { return i2cAddress; }
	public int getI2cBus() { return i2cBus; }
	public I2CDevice getDevice() { return device; }
	
	@Override synchronized public byte[] writeThenRead(byte[] request, int start, int length, int readLenght, Byte until) {
		assert readLenght >= 0 && readLenght <= reply.length;
		assert until == null : "until not supported";
		open();
		return funcGuarded(eh,() -> {try {
			if (request == null) {
				byte[] response = Arrays.copyOfRange(reply, 0, device.read(reply, 0, readLenght));
				log.fine("read: 0x"+printHexBinary(response));
				return response;
			} if (readLenght == 0) {
				device.write(request, start, length);
				log.fine("wrote: 0x"+printHexBinary(Arrays.copyOfRange(request, start, start+length)));
				return new byte[0];
			} else {
				byte[] response = Arrays.copyOfRange(reply, 0, device.read(request, start, length, reply, 0, readLenght));
				log.fine("read+wrote: 0x"+printHexBinary(Arrays.copyOfRange(request, start, start+length))+" --> 0x"+printHexBinary(response));
				return response;
			}
		} catch (IOException e) { throw error(log ,e, 10102, minor, "Error reading/writing i2c >{0}< - {1}", getUrl().urlString,e.getMessage()); }});
	}

	@Override
	public void flush() {
		open();
		try { device.read(); } catch (Exception e) { reallySleep(sleepAfterFlushMillis); } // will also wake up some devices
		log.fine("flushed");
	}

}
