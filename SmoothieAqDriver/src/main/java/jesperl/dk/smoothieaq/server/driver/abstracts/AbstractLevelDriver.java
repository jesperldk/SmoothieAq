package jesperl.dk.smoothieaq.server.driver.abstracts;

import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.util.shared.error.Severity.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.util.*;
import java.util.logging.*;

import jesperl.dk.smoothieaq.server.access.classes.*;
import jesperl.dk.smoothieaq.server.driver.classes.*;
import jesperl.dk.smoothieaq.util.shared.error.*;
import jesperl.dk.smoothieaq.util.shared.*;

public abstract class  AbstractLevelDriver<S extends AbstractDriver.Storage, D extends DeviceAccess> extends AbstractDriver<S,D> implements LevelDriver {
	private final static Logger log = Logger.getLogger(AbstractLevelDriver.class .getName());

	@Override protected int calibrationUse() { return maxIdx() + 1; }
	protected final int maxIdx() { return super.calibrationUse(); }
	@Override protected void initCalibration(float[] calibration) { calibration[maxIdx()] = 100; super.initCalibration(calibration); }
	
	@Override
	public void level(int startAtMinutes, LevelProgram program) {
		if (!isSimulate()) dolevel(startAtMinutes,program);
		log.info("Starting at "+startAtMinutes+" in program "+program);
	}
	
	@Override
	public void on(float level) {
		if (!isSimulate()) onlevel(level);
		log.info("Turned on at "+level+" ("+getUrl()+")");
	}
	
	@Override
	public void off() {
		if (!isSimulate()) offlevel();
		log.info("Turned off ("+getUrl()+")");
	}
	
	@Override public float getMaxLevel() { return funcStorage(s -> s.calibration[maxIdx()]); }
	
	protected float scale(float level) {
		float max = funcStorage(s -> s.calibration[maxIdx()]);
		if (level < 0 || level > max) throw error(log,20093,major,"A level of {0} is not valid, must be in [{1}-{2}]",level,0,max);
		return level/max;
	}
	
	protected abstract void dolevel(int startAtMinutes, LevelProgram program);
	protected abstract void onlevel(float level);
	protected abstract void offlevel();

	@Override public StepInfo[] calibrationInfo() {
		return funcStorage(s ->
			array(
				new StepInfo(1, 
				array(new StepInfoField(msg(20090,"Maximal level of device (in your chosen units, default is 100%)"),s.calibration[maxIdx()])), 
				msg(20091,
					"Internally this driver uses as level a fraction (0-100%) of the maximal level of the actual device.<p> "+
					"If you want to express the level more directly (eg in lumens for a lamp fixture), "+
					"you should below specify the maximum level your device is capable of (eg 2200 lumen). "+
					"When using this driver you must then specify the level in these units (eg 0-2200 lumen)."
				))
			)
		);
	}
	@Override public Pair<List<Message>,float[]> calibrateStep(int stepId, float[] stepValues, float[] calibration) { 
		if (stepId != 1) return null; 
		if (stepValues[0] <= 0f) throw error(log,20092,major,"That is not an meaningsfull maximum level");
		calibration[maxIdx()] = stepValues[0];
		return null;
	}
}
