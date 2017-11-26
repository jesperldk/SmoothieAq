package jesperl.dk.smoothieaq.server.driver;

import static jesperl.dk.smoothieaq.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.shared.util.Objects.*;

import java.util.*;

import jesperl.dk.smoothieaq.server.access.*;
import jesperl.dk.smoothieaq.server.access.abstracts.*;
import jesperl.dk.smoothieaq.server.driver.abstracts.*;
import jesperl.dk.smoothieaq.server.driver.classes.*;
import jesperl.dk.smoothieaq.shared.error.*;

public class SaqLevelDriver extends AbstractLevelDriver<AbstractOnoffDriver.Storage,SaqDeviceAccess> {

	@Override public void init(DeviceAccessContext context, String urlString, float[] calibration) {
		init(context, urlString, SaqDeviceAccess.class, SaqLevelDriver.class, () -> new Storage(), calibration);
	}
	
	@Override public Message name() { return msg(20321,"Generic Saq level device"); }
	@Override public Message description() { return msg(20322,"A device connected to an external Smoothie Aq Arduino box"); }
	
	@Override public List<String> getDefaultUrls(DeviceAccessContext context) { 
		return SaqDeviceAccess.enumerateUrlString(context, SaqDeviceAccess.levelCls, -1); 
	}

	@Override protected void dolevel(int startAtMinutes, LevelProgram program) {
		// TODO validation
		StepProgram steps = (StepProgram) program;
		List<String> args = new ArrayList<>();
		args.add(strv(startAtMinutes));
		for (int i = 0; i < steps.stepDurationMinutes.length; i++) {
			args.add(strv(steps.stepDurationMinutes[i]));
			args.add(strv(saqLevel(steps.stepEndLevel[i])));
		}
		useDeviceAccess(da -> da.okLogical(SaqbDeviceAccess.levelCmd,array(args)));
	}

	@Override protected void onlevel(float level) {
		// TODO proper onlevel in SmoothieAqArduino
		useDeviceAccess(da -> da.okLogical(SaqbDeviceAccess.levelCmd,"0",strv(24*60-1),strv(saqLevel(level))));
	}

	@Override protected void offlevel() { useDeviceAccess(da -> da.okLogical(SaqbDeviceAccess.offCmd)); }
	
	protected int saqLevel(float level) { return (int) (scale(level)*1000); }

}
