package jesperl.dk.smoothieaq.server.driver;

import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;

import java.util.*;

import jesperl.dk.smoothieaq.server.access.*;
import jesperl.dk.smoothieaq.server.access.abstracts.*;
import jesperl.dk.smoothieaq.server.driver.abstracts.*;
import jesperl.dk.smoothieaq.server.driver.classes.*;
import jesperl.dk.smoothieaq.util.shared.error.*;
import sun.reflect.generics.reflectiveObjects.*;

public class  SaqOnoffDriver extends AbstractOnoffDriver<AbstractOnoffDriver.Storage,SaqDeviceAccess> implements StatusDriver {

	@Override public void init(DeviceAccessContext context, String urlString, float[] calibration) {
		init(context, urlString, SaqDeviceAccess.class , SaqOnoffDriver.class , () -> new Storage(), calibration);
	}
	
	@Override public Message name() { return msg(20301,"Generic Saq ON/OFF device"); }
	@Override public Message description() { return msg(20302,"A device connected to an external Smoothie Aq Arduino box"); }
	
	@Override public List<String> getDefaultUrls(DeviceAccessContext context) { 
		return SaqDeviceAccess.enumerateUrlString(context, SaqDeviceAccess.onoffCls, -1); 
	}

	@Override protected void on() { useDeviceAccess(da -> da.okLogical(SaqbDeviceAccess.onCmd)); }
	@Override public void off() { useDeviceAccess(da -> da.okLogical(SaqbDeviceAccess.offCmd)); }

	@Override public void blink(int level) {
		throw new NotImplementedException(); // TODO Auto-generated method stub
	}

}
