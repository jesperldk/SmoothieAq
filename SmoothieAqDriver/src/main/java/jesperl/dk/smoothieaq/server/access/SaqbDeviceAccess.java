package jesperl.dk.smoothieaq.server.access;

import static jesperl.dk.smoothieaq.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.shared.error.Severity.*;
import static jesperl.dk.smoothieaq.shared.util.Objects.*;

import java.util.*;
import java.util.logging.*;

import com.fazecast.jSerialComm.*;

import jesperl.dk.smoothieaq.server.access.abstracts.*;
import jesperl.dk.smoothieaq.server.access.classes.*;
import jesperl.dk.smoothieaq.shared.util.*;

public class SaqbDeviceAccess extends CmdserDeviceAccess {
	private final static Logger log = Logger.getLogger(SaqbDeviceAccess.class.getName());

	public static String bus = bus(SaqbDeviceAccess.class);

	public static int versionCmd = 1;     // -> versionReply
	public static int timeCmd = 2;        // -> timeReply
	public static int buzzCmd = 3; 		// buzz-millies [, pause-millies, buzz-millies ]... -> okReply
	public static int onCmd = 4;          // logical-no -> okReply
	public static int offCmd = 5;         // logical-no -> okReply
	public static int doCmd = 6;          // logical-no, seconds -> okReply
	public static int valueCmd = 7;       // logical-no -> valueReply
	public static int levelCmd = 8;       // logical-no, start-at {, minutes, level }... -> okReply; (level must be 1-999)
	public static int statusCmd = 9;    	// logical-no -> onReply | offReply | doReply | levelReply
	public static int confCmd = 10;       // -> confReply
	public static int blinkCmd = 11;      // on-millies [, off-millies, on-millies ]... -> okReply

	public static int okReply = 100;     //
	public static int errorReply = 199;   // error-message
	public static int versionReply = 101; // "SmoothieAq", smoothieAqArduinoVer, sketch-name, sketch-ver
	public static int timeReply = 102;    // hh,mm,ss
	public static int valueReply = 107;   // logical-no, value
	public static int onReply = 159;      // logical-no
	public static int offReply = 169;     // logical-no
	public static int doReply = 179;      // logical-no, done-seconds, total-seconds
	public static int levelReply = 189;   // cur-level, done-minutes, total-minutes
	public static int confReply = 110;    // { CmdType, DevType, l }...

	static public int baud = 50000;
	
	public static class Storage {
		public Pair<String, String> saqbe = null;
	}

	// URL: saqb://smoothieaq-name
	@Override protected DeviceUrl getWrapperUrl(DeviceAccessContext context, DeviceUrl daUrl) {
		for (Pair<DeviceUrl,String> e: context.enumerate(bus))
			if (daUrl.busSelector.equals(e.a.busSelector))
				return super.getWrapperUrl(context, DeviceUrl.create(DeviceUrl.url(CmdserDeviceAccess.bus, e.b, array(strv(baud)), null, null)));
		throw error(log,10410,major,"Could not find >{0}<",daUrl.urlString);
	}

	public void okCmd(int cmdNo, String... args) { doCmd(okReply, cmdNo, args); }
	
	static public List<Pair<DeviceUrl,String>> enumerate(DeviceAccessContext context) {
		List<Pair<DeviceUrl,String>> urls = new ArrayList<>();
		for (SerialPort somePort: SerialPort.getCommPorts()) {
			Pair<String,String> saqbe = saqbCheck(context,somePort);
			if (saqbe != null && saqbe.a != null) urls.add(pair(DeviceUrl.create(DeviceUrl.url(SaqbDeviceAccess.bus, saqbe.a, null)),saqbe.b));
		}
		return urls;
	}

	protected static Pair<String, String> saqbCheck(DeviceAccessContext context, SerialPort somePort) {
		return funcNoException(() -> {
			log.fine("Checking for saqd on "+somePort.getDescriptivePortName()+"/"+somePort.getSystemPortName());
			String urlString = DeviceUrl.url(CmdserDeviceAccess.bus, somePort.getSystemPortName(), array(strv(baud)), null, null);
			CmdserDeviceAccess cmdDa = context.get(CmdserDeviceAccess.class, urlString);
			try { synchronized (cmdDa) {
				cmdDa.setNoretries(true);
				Storage storage = cmdDa.retrieveOrCreate(SaqbDeviceAccess.class, Storage::new);
				if (storage.saqbe != null) return storage.saqbe;
				storage.saqbe = pair(null,null); // because we may fail below
				String[] ver = cmdDa.doCmd(versionReply,versionCmd);
				if (!"SmoothieAq".equals(ver[0])) return null;
				storage.saqbe = pair(ver[2],somePort.getSystemPortName());
				return storage.saqbe;
			}} finally { doNoException(() -> cmdDa.setNoretries(false)); doNoException(() -> cmdDa.release()); }
		},null);
	}

}
