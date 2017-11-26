package jesperl.dk.smoothieaq.server.driver.abstracts;

import static jesperl.dk.smoothieaq.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.shared.error.Severity.*;
import static jesperl.dk.smoothieaq.shared.util.Objects.*;

import java.util.*;
import java.util.logging.*;

import jesperl.dk.smoothieaq.server.access.classes.*;
import jesperl.dk.smoothieaq.server.driver.classes.*;
import jesperl.dk.smoothieaq.shared.error.*;
import jesperl.dk.smoothieaq.shared.util.*;

public abstract class AbstractDoserDriver<S extends AbstractOnoffDriver.Storage, D extends DeviceAccess> extends AbstractOnoffDriver<S,D> implements DoserDriver {
	private final static Logger log = Logger.getLogger(AbstractDoserDriver.class.getName());

	@Override protected int calibrationUse() { return secondsIdx() + 1; }
	protected final int amountIdx() { return super.calibrationUse(); }
	protected final int secondsIdx() { return amountIdx() + 1; }
	
	@Override
	public void dose(float amount) {
		useStorage(s -> {
			if (isSimulate()) return;
			int milis = (int) (amount * s.calibration[secondsIdx()]/s.calibration[amountIdx()]);
			dose(milis);
			log.info("dosing amount "+amount+" in "+(milis/1000)+" seconds ("+getUrl()+")");
		});
	}

	protected abstract void dose(int milis);

	@Override public StepInfo[] calibrationInfo() {
		return funcStorage(s ->
			array(
				new StepInfo(1, 
				array(new StepInfoField(msg(20080,"Amount to dose for the calibration"),s.calibration[amountIdx()])), 
				msg(20081,
					"A doser works by dispensing a substance in a certain amount of time. "+
					"To be able to specify the dose in amount of substance instead in amount of time, we must calibrate the doser.<p> "+
					"<li>First make sure that the doser container is full with plenty test substance.</li>"+
					"<li>You should then set up the doser, so it doses either in measuring cup (for fluids) or in a cup on a weight (for solids).</li>"+
					"<li>You are going to deside an amount to dose for the calibration; choose an amount that you expects will take in the area of 30 to 60 seconde. "+
					"Don't worry, you can always redo the calibration if you chooses wrongly. Enter the amount chosen below.</li>"+
					"<li>You should use the same unit of measure that you want to use when you later specify how much to realy dose; usually you should use ml or g.</li>"+
					"<li>When you press the button below, the doser will start dosing, and the calibration program will move to the next step.</li>"+
					"<li>On the next step, you must watch the measuring cup or the weight and press the button on that step when you have dosed the chosen amount.</li>"
				)),
				new StepInfo(2, 
				0, 
				msg(20082,
					"Watch the measuring cup/the weight and press the button when you have dosed the chosen amount."
				))
			)
		);
	}
	@Override public Pair<List<Message>,float[]> calibrateStep(int stepId, float[] stepValues, float[] calibration) { 
		if (stepId == 1) return pair(calibrate1(stepValues, calibration), calibration); 
		if (stepId == 2) return pair(calibrate2(stepValues, calibration), calibration);
		return null;
	}
	protected List<Message> calibrate1(float[] stepValues, float[] calibration) {
		if (stepValues[0] <= 0f) throw error(log,20083,major,"That is not an meaningsfull amount");
		calibration[amountIdx()] = stepValues[0];
		onoff(true);
		calibration[secondsIdx()] = System.currentTimeMillis();
		return null;
	}
	private List<Message> calibrate2(float[] stepValues, float[] calibration) {
		onoff(false);
		calibration[secondsIdx()] = (System.currentTimeMillis() - calibration[secondsIdx()]) / 1000;
		List<Message> msgs = msgs();
		if (calibration[secondsIdx()] < 10 || calibration[secondsIdx()] > 300)
			msgs.add(msg(20083,"You calibrated for {0} seconds - that is realy not a good value, you should redo and aim for an amount that will take in the area of 30 to 60 seconds",calibration[secondsIdx()]));
		float unitsPerSeconds = calibration[amountIdx()]/calibration[secondsIdx()];
		msgs.add(msg(20084,"Your doser will after finalization of the calibration dose {0} units/second",unitsPerSeconds));
		if (unitsPerSeconds < 0.1 || unitsPerSeconds > 10)
			msgs.add(msg(20085,"{0} units/second seems a bid odd - are you shure the calibration went well?",unitsPerSeconds));
		log.info("doser calibration amount="+calibration[amountIdx()]+" seconds"+calibration[secondsIdx()]+" units/second="+unitsPerSeconds);
		return msgs;
	}
	
	@Override public float getAmountPerSec() { return funcStorage(s -> s.calibration[amountIdx()]/s.calibration[secondsIdx()]); }
}
