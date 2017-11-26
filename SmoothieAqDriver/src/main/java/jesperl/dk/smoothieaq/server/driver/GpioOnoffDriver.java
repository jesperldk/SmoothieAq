package jesperl.dk.smoothieaq.server.driver;

import static jesperl.dk.smoothieaq.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.shared.util.Objects.*;

import java.util.*;

import jesperl.dk.smoothieaq.server.access.*;
import jesperl.dk.smoothieaq.server.access.abstracts.*;
import jesperl.dk.smoothieaq.server.access.classes.*;
import jesperl.dk.smoothieaq.server.driver.abstracts.*;
import jesperl.dk.smoothieaq.server.driver.classes.*;
import jesperl.dk.smoothieaq.shared.error.*;
import sun.reflect.generics.reflectiveObjects.*;

public class GpioOnoffDriver extends AbstractOnoffDriver<AbstractOnoffDriver.Storage,GpioDeviceAccess> implements StatusDriver {

	@Override public void init(DeviceAccessContext context, String urlString, float[] calibration) {
		init(context, urlString, GpioDeviceAccess.class, GpioOnoffDriver.class, () -> new Storage(), calibration);
	}
	
	@Override public Message name() { return msg(20401,"Generic GPIO ON/OFF device"); }
	@Override public Message description() { return msg(20402,"A device connected directly to the GPIO pins"); }
	
	@Override public List<String> getDefaultUrls(DeviceAccessContext context) { 
		return list(DeviceUrl.url(PiDeviceAccess.bus, "gpio", null, "_pinNo_", array("1"))); 
	}

	@Override protected void on() { useDeviceAccess(da -> da.setDigital(true)); }
	@Override public void off() { useDeviceAccess(da -> da.setDigital(false)); }

	@Override public void blink(int level) {
		throw new NotImplementedException(); // TODO Auto-generated method stub
	}

}
