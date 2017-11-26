package jesperl.dk.smoothieaq.server.access;

import static com.fazecast.jSerialComm.SerialPort.*;
import static jesperl.dk.smoothieaq.util.server.Utils.*;
import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.util.shared.error.Severity.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.util.*;
import java.util.logging.*;

import com.fazecast.jSerialComm.*;

import jesperl.dk.smoothieaq.server.access.abstracts.*;
import jesperl.dk.smoothieaq.server.access.classes.*;
import jesperl.dk.smoothieaq.util.shared.*;

public class  SerialDeviceAccess extends AbstractDeviceAccess implements ByteDeviceAccess {
	private final static Logger log = Logger.getLogger(SerialDeviceAccess.class .getName());
	
	public static String bus = bus(SerialDeviceAccess.class );

	public long waitAfterOpenMillis = 200;
	public int sleepAfterFlushMillis = 50;
	public int sleepAfterWriteMillis = 10;
	public int timeoutOnFullReadMillis = 200;

	private String portDescriptor;
	private int baudRate;
	SerialPort port = null;
	private byte[] reply = new byte[2048]; 
	
	// URL: usbserial://ser-port-name:baud-rate.1-no-retry
	@Override public ByteDeviceAccess init(DeviceAccessContext context, DeviceUrl url) {
		super.init(context, url);
		doGuardedX(() -> {
			portDescriptor = url.busSelector;
			baudRate = url.deviceArgs.length > 0 ? Integer.parseInt(url.deviceArgs[0]) : 9600;
		}, e -> error(log,e,10300, major, "Malformed deviceAccess url: {0}",url.urlString));
		return this;
	}
	
	@Override protected void openIt() {
		doGuardedX(eh,() -> {try {
			for (SerialPort somePort: SerialPort.getCommPorts()) {
				if (somePort.getSystemPortName().equalsIgnoreCase(portDescriptor)) {
					port = somePort;
					log.fine("found port: "+somePort.getDescriptivePortName()+"/"+somePort.getSystemPortName()+" - match!!");
				} else {
					log.finer("found port: "+somePort.getDescriptivePortName()+"/"+somePort.getSystemPortName()+" - no match");
				}
			}
			if (port == null) throw new RuntimeException("port not found "+portDescriptor);
			if (!port.openPort()) { port = null; throw error(log, 10302, medium, "Error opening serial >{0}<", getUrl().urlString); }
			port.setComPortParameters(baudRate, 8, ONE_STOP_BIT, NO_PARITY);
			port.setFlowControl(FLOW_CONTROL_DISABLED);
			port.setComPortTimeouts(TIMEOUT_NONBLOCKING, 100, 100);
			log.info("Opened "+getUrl().urlString);
			reallySleep(waitAfterOpenMillis);
		} catch (Exception e) { if (port != null && port.isOpen()) port.closePort(); throw e;}
		}, e -> error(log, e, 10301, medium, "Error opening serial >{0}< - {1}", getUrl().urlString,e.getMessage()));
		super.openIt();
	}
	
	@Override
	protected void closeIt() {
		super.closeIt();
		port.closePort();
	}
	
	public String getPortDescriptor() { return portDescriptor; }
	public int getBaudRate() { return baudRate; }
	public SerialPort getPort() { return port; }
	
	@Override synchronized public byte[] writeThenRead(byte[] request, int start, int length, int readLenght, Byte until) {
		assert readLenght >= 0 && readLenght <= reply.length;
		assert start == 0;
		open();
		flush();
		return funcGuardedX(eh,() -> {
			if (length > 0) {
				if (port.writeBytes(request, length) < length) throw error(log, 10304, minor,"Could not write on Serial >{0}<",getUrl().urlString);
				log.fine("wrote: "+new String(request,0,length));
				reallySleep(sleepAfterWriteMillis);
				if (readLenght == 0 && until == null) return new byte[0];
			}
			
			long startTime = System.currentTimeMillis(); 
			StringBuffer replyBuf = new StringBuffer();
			
			while (true) {
				int read = port.readBytes(reply, reply.length);
				if (read == -1) {
					throw error(log, 10305, minor,"Error reading on Serial >{0}<",getUrl().urlString);
				} else if (read == 0) {
					reallySleep(sleepAfterWriteMillis);
				} else {
					log.fine("read: "+new String(reply,0,read));
					for (int p = 0; p < read; p++)
						if ((readLenght > 0 && replyBuf.length()+p+1 == readLenght) || (until != null && reply[p] == until)) 
							return replyBuf.append(new String(reply, 0, p+1)).toString().getBytes();
					replyBuf.append(new String(reply, 0, read));
				}
				if (System.currentTimeMillis() > startTime+timeoutOnFullReadMillis) throw error(log, 10306, minor,"Timeout on Serial >{0}<",getUrl().urlString);
			}
		}, e -> error(log ,e, 10303, minor, "Error reading/writing Serial >{0}< - {1}", getUrl().urlString,e.getMessage()));
	}

	@Override public void flush() {
		open();
		doNoException(() -> { while (port.readBytes(reply, reply.length) > 0); });
		reallySleep(sleepAfterFlushMillis);
		log.fine("flushed");
	}

	static public List<Pair<DeviceUrl,String>> enumerate(DeviceAccessContext context) {
		List<Pair<DeviceUrl,String>> urls = new ArrayList<>();
		for (SerialPort somePort: SerialPort.getCommPorts()) 
			urls.add(pair(DeviceUrl.create(DeviceUrl.url(bus, somePort.getSystemPortName(), null)),somePort.getDescriptivePortName()));
		return urls;
	}
}
