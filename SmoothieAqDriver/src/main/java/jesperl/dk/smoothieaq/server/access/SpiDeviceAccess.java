package jesperl.dk.smoothieaq.server.access;

import static javax.xml.bind.DatatypeConverter.*;
import static jesperl.dk.smoothieaq.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.shared.error.Severity.*;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import com.pi4j.io.spi.*;

import jesperl.dk.smoothieaq.server.access.abstracts.*;
import jesperl.dk.smoothieaq.server.access.classes.*;

public class SpiDeviceAccess extends AbstractDeviceAccess implements ByteDeviceAccess {
	private final static Logger log = Logger.getLogger(SpiDeviceAccess.class.getName());

	public static String bus = bus(SpiDeviceAccess.class);

	private int spiBus; // only 0 currently
	private SpiChannel spiChannel; // CS0 or CS1 only
	private int speed;
	private SpiMode mode;
	private SpiDevice device;
	private byte[] reply = new byte[2048]; 
	
	// URL: spi://pi-spi-bus-no/cs-no:speed.mode  -- only supports pi-spi-bus-no=0
	@Override public ByteDeviceAccess init(DeviceAccessContext context, DeviceUrl url) {
		super.init(context, url);
		doGuardedX(eh,() -> {
			spiBus = Short.parseShort(url.busSelector);
			if (spiBus != 0) throw error(log,10101, major, "only supports SPI bus 0");
			spiChannel = SpiChannel.getByNumber(Short.parseShort(url.deviceSelector));
			speed = url.deviceArgs.length > 0 ? Integer.parseInt(url.deviceArgs[0]) : SpiDevice.DEFAULT_SPI_SPEED;
			mode = url.deviceArgs.length > 1 ? SpiMode.getByNumber(Integer.parseInt(url.deviceArgs[1])) : SpiDevice.DEFAULT_SPI_MODE;
		}, e -> error(log,e,10200, major, "Malformed SPI url: {0}",url.urlString));
		return this;
	}
	
	@Override protected void openIt() {
		doGuarded(eh,() -> {try {
			device = SpiFactory.getInstance(spiChannel,speed,mode);
			log.info("Opened "+getUrl().urlString);
		} catch (IOException e) { throw error(log ,e, 10201, medium, "Error opening SPI >{0}< - {1}", getUrl().urlString,e.getMessage()) ; }});
		super.openIt();
	}
	
	public SpiChannel getSpiChannel() { return spiChannel; }
	public int getSpeed() { return speed; }
	public SpiMode getMode() { return mode; }
	public SpiDevice getDevice() { return device; }
	
	@Override public byte[] writeThenRead(byte[] request, int start, int length, int readLenght, Byte until) {
		assert readLenght >= 0 && readLenght <= reply.length;
		assert until == null : "until not supported";
		open();
		return funcGuarded(eh,() -> {try {
			if (request == null) {
				throw error(log, 10203, major, "cannot only read SPI >{0}<",getUrl().urlString);
			} if (readLenght == 0) {
				device.write(request, start, length);
				log.fine("wrote: 0x"+printHexBinary(Arrays.copyOfRange(request, start, start+length))+" --> 0x");
				return new byte[0];
			} else {
				byte[] response = device.write(request, start, length);
				log.fine("read+wrote: 0x"+printHexBinary(Arrays.copyOfRange(request, start, start+length))+" --> 0x"+printHexBinary(response));
				return response;
			}
		} catch (IOException e) { throw error(log ,e, 10202, minor, "Error reading/writing SPI >{0}< - {1}", getUrl().urlString,e.getMessage()); }});
	}

	@Override public void flush() {
		open();
		log.fine("flushed (noop)");
	}

}
