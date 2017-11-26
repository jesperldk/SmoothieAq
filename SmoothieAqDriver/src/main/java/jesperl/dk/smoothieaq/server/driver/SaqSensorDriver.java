package jesperl.dk.smoothieaq.server.driver;

import static jesperl.dk.smoothieaq.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.shared.error.Severity.*;
import static jesperl.dk.smoothieaq.shared.util.Objects.*;

import java.util.*;
import java.util.logging.*;

import jesperl.dk.smoothieaq.server.access.*;
import jesperl.dk.smoothieaq.server.access.abstracts.*;
import jesperl.dk.smoothieaq.server.driver.abstracts.*;
import jesperl.dk.smoothieaq.shared.error.*;

public class SaqSensorDriver extends AbstractSingleSensorDriver<SaqDeviceAccess> {
	private final static Logger log = Logger.getLogger(SaqSensorDriver.class.getName());

	@Override public void init(DeviceAccessContext context, String urlString, float[] calibration) {
		init(context, urlString, SaqDeviceAccess.class, SaqSensorDriver.class, () -> new Storage(), calibration);
	}
	
	@Override public Message name() { return msg(20331,"Generic Saq sensor device"); }
	@Override public Message description() { return msg(20332,"A device connected to an external Smoothie Aq Arduino box"); }
	
	@Override public List<String> getDefaultUrls(DeviceAccessContext context) { 
		return SaqDeviceAccess.enumerateUrlString(context, SaqDeviceAccess.measureCls, -1); 
	}

	@Override protected float justMeasure() {
		return funcGuardedX(() -> funcDeviceAccess(da ->
			floatv(da.doLogical(SaqbDeviceAccess.valueReply,SaqbDeviceAccess.valueCmd)[0]) 
		), e -> error(log,e,20333,major,"Error in reply from device {0}: ",getUrl(),e.getMessage()));
	}

}
