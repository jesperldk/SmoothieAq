package jesperl.dk.smoothieaq.server.driver;

import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;

import java.util.*;

import jesperl.dk.smoothieaq.server.access.*;
import jesperl.dk.smoothieaq.server.access.abstracts.*;
import jesperl.dk.smoothieaq.server.driver.abstracts.*;
import jesperl.dk.smoothieaq.util.shared.error.*;

public class  SaqDoserDriver extends AbstractDoserDriver<AbstractDoserDriver.Storage,SaqDeviceAccess> {

	@Override public void init(DeviceAccessContext context, String urlString, float[] calibration) {
		init(context, urlString, SaqDeviceAccess.class , SaqDoserDriver.class , () -> new Storage(), calibration);
	}
	
	@Override public Message name() { return msg(20311,"Generic Saq doser device"); }
	@Override public Message description() { return msg(20312,"A device connected to an external Smoothie Aq Arduino box"); }
	
	@Override public List<String> getDefaultUrls(DeviceAccessContext context) { 
		return SaqDeviceAccess.enumerateUrlString(context, SaqDeviceAccess.onoffCls, SaqDeviceAccess.doserDev); 
	}

	@Override protected void on() { useDeviceAccess(da -> da.okLogical(SaqbDeviceAccess.onCmd)); }
	@Override protected void off() { useDeviceAccess(da -> da.okLogical(SaqbDeviceAccess.offCmd)); }
	@Override protected void dose(int milis) { useDeviceAccess(da -> da.okLogical(SaqbDeviceAccess.doCmd,Integer.toString(milis/1000))); }

}
