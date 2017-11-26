package jesperl.dk.smoothieaq.server.access;

import jesperl.dk.smoothieaq.server.access.abstracts.*;
import jesperl.dk.smoothieaq.server.access.classes.*;


public class CmdserDeviceAccess extends AbstractCmdDeviceAccess {

	public static String bus = bus(CmdserDeviceAccess.class);

	// URL: cmdser://ser-port-name:baud-rate
	@Override protected DeviceUrl getWrapperUrl(DeviceAccessContext context, DeviceUrl daUrl) {
		daUrl.bus = SerialDeviceAccess.bus;
		daUrl.rebuildUrlString();
		return daUrl;
	}
}
