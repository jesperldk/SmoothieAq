package jesperl.dk.smoothieaq.server.driver;

import static jesperl.dk.smoothieaq.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.shared.util.Objects.*;

import java.util.*;
import java.util.function.*;

import jesperl.dk.smoothieaq.server.access.*;
import jesperl.dk.smoothieaq.server.access.abstracts.*;
import jesperl.dk.smoothieaq.server.access.classes.*;
import jesperl.dk.smoothieaq.server.driver.abstracts.*;
import jesperl.dk.smoothieaq.shared.error.*;

public class GpioToggleDriver extends AbstractToggleDriver<AbstractToggleDriver.Storage,GpioDeviceAccess> {

	@Override public void init(DeviceAccessContext context, String urlString, float[] calibration) {
		init(context, urlString, GpioDeviceAccess.class, GpioToggleDriver.class, () -> new Storage(), calibration);
	}
	
	@Override public Message name() { return msg(20411,"Generic GPIO switch device"); }
	@Override public Message description() { return msg(20412,"A device connected directly to the GPIO pins"); }
	
	@Override public List<String> getDefaultUrls(DeviceAccessContext context) { 
		return list(DeviceUrl.url(PiDeviceAccess.bus, "gpio", null, "_pinNo_", array("1"))); 
	}

	@Override protected boolean measureOn() { return funcDeviceAccess(da -> da.getDigital()); }
	@Override protected Object addListener(Consumer<Boolean> listener) { return funcDeviceAccess(da -> da.listenDigital(listener)); }
	@Override protected void removeListener(Object listenerKey, Consumer<Boolean> listener) { useDeviceAccess(da -> da.stopListen(listenerKey)); }

}
