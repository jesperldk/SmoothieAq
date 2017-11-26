package jesperl.dk.smoothieaq.server.driver.abstracts;

import static java.lang.Math.*;
import static jesperl.dk.smoothieaq.util.server.Utils.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;
import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.util.shared.error.Severity.*;

import java.util.*;
import java.util.function.*;
import java.util.logging.*;

import jesperl.dk.smoothieaq.server.access.classes.*;
import jesperl.dk.smoothieaq.util.shared.*;
import jesperl.dk.smoothieaq.util.shared.error.*;

/**
 * You must subclass it with something that can measure temp and phVolt.
 * Handles calculation and calibration largely after
 *   http://www.wetnewf.org/pdfs/ph-meter-calibration.html
 * With a bit extra glanced from
 *   http://www2.emersonprocess.com/siteadmincenter/PM%20Rosemount%20Analytical%20Documents/LIQ_MAN_6033_Theory_Practice_pH_Measurement.pdf 		
 */
public abstract class  AbstractPhSensorDriver<S extends AbstractPhSensorDriver.Storage, D extends DeviceAccess> extends AbstractSensorDriver<S,D> {
	private final static Logger log = Logger.getLogger(AbstractPhSensorDriver.class .getName());
	
	public int timesToMeasureForAverage = 3;
	public int maxTimesToMeasureForStableCalibration = 60;
	public int milliesBetweenMeasureForStableCalibration = 1000;
	public float stableThreashold1 = 0.01f; // in pH
	public float stableThreashold2 = 0.05f; // in pH
	
	public static final float pHi = 7f; // neutral pH for probe
	public static final float maxSlope = 59.16f; // 100%, acceptable is 90% - 105%, can be used is 80% - 105%
	public static final float optimalOffset = 0f; // acceptable is -25mV - 25mV
	public static final float rByF = 0.1984f; // = R/F = gas constant / Faraday constant

	public static final String[] bufferTemps = new String[] {"15°C","20°C","25°C","30°C","35°C"};

	public static class  CalibrationMeasure { // Calibration temp values
		public Buffer buffer; // tempK -> pH
		public float e;
		public float t;
		public float eSpan;
	}

	public static class  Storage extends AbstractSensorDriver.Storage {
		public float temp;
		public float pHVolt;
		public float pH;
		public CalibrationMeasure c1;
		public CalibrationMeasure c2;
	}

	public interface Buffer extends Function<Float,Float> {}
	public static class  SiBuffer implements Buffer {
		SplineInterpolator si;
		public SiBuffer(List<Float> pH) { si = SplineInterpolator.createMonotoneCubicSpline(list(15.0f,20.0f,25.0f,30.0f,35.0f), pH); }
		@Override public Float apply(Float t) { return si.interpolate(t); }
	}

	@Override protected int calibrationUse() { return sIdx() + 1; }
	protected final int buf1Idx() { return super.calibrationUse() + 0; }
	protected final int buf2idx() { return buf1Idx() + bufferTemps.length; }
	protected final int e0Idx() { return buf2idx() + bufferTemps.length; }
	protected final int sIdx() { return e0Idx() + 1; }
	
	@Override protected void initCalibration(float[] calibration) { super.initCalibration(calibration);	} // TODO init with default pH4 and pH7 buffers

	@Override protected void measureAndStore(S s) { 
		float phVoltSum = 0.0f;
		float tempSum = 0.0f;
		for (int i = 0; i < timesToMeasureForAverage; i++) {
			measureAndStoreSingle(s);
			tempSum += s.temp;
			phVoltSum += s.pHVolt;
		}
		s.temp = tempSum / timesToMeasureForAverage;
		s.pHVolt = phVoltSum / timesToMeasureForAverage;
		s.pH = pH(s.pHVolt,s.temp,s.calibration[e0Idx()],s.calibration[sIdx()]);
		log.fine("measured "+s.temp+"°C "+(s.pHVolt/1000)+"mV "+s.pH+"pH (E0="+s.calibration[e0Idx()]+"mV s="+s.calibration[sIdx()]+"mV/pH)");
	}

	/**
	 * Measure temp (degree C!) and pHVolt (not mV!) and then store it. Only do one measurement - no averaging of pHVolt!
	 * @param s The storage, where you should store the measurement
	 */
	protected abstract void measureAndStoreSingle(S s);
	protected float phEndCalibration() { return 1; } // you should probably never override this 
	
	@Override protected StepInfo[] measureCalibrationInfo() {
		return array(
			new StepInfo(1, fieldInfos(1), msg(20061,
					"A pH probe needs quite frequent calibration, every few weeks is good, several months are bad. Also, a pH probe "+
					"has a limited life, usually one or a few years, depending on the quality of the probe. This calibration process "+
					"will adjust the pH calculations for the best precision, and it will also give you an indication of wether the probe "+
					"needs cleaning or if it is nearing its end of life.<p>"+
					"This driver supports 2-step calibration only - that means you must have to different pH calibrations buffers ready. "+
					"It is common to use pH4 and pH7 buffers, or perhaps pH7 and pH10 buffers, but this driver supports any two buffers. "+
					"For each of the two buffers, you must know their pH value for each of the temperatures 15°C, 20°C, 25°C, 30°C and 35°C. "+
					"This information can usaully be found on the bottle (else use google). You should know the pH values with at least two "+
					"decimals.<p><p>"+
					"Enter the values for the <b>first</b> buffer below. Then dip the probe in a sample of the buffer solution (never reuse the "+
					"buffer sample), and press the button to start the measure of the buffer. This can take a little while, because we will "+
					"wait for the probe to measure a stable value."
			)),
			new StepInfo(2, fieldInfos(2), msg(20062,
					"Enter the values for the <b>second</b> buffer below. Then dip the probe in a sample of the buffer solution (never reuse the "+
					"buffer sample), and press the button to start the measure of the buffer. This can take a little while, because we will "+
					"wait for the probe to measure a stable value."
			))
		);
	}
	protected StepInfoField[] fieldInfos(int stepNo) {
		StepInfoField[] fields = new StepInfoField[bufferTemps.length];
		int idx = stepNo == 1 ? buf1Idx() : buf2idx();
		useStorage(s -> {
			for (int i = 0; i < bufferTemps.length; i++) 
				fields[i] = new StepInfoField(msg(20063,"pH value for buffer {0} at temperature {1}",stepNo,bufferTemps[i]),s.calibration[idx+i]);
		});
		return fields;
	}
	@Override protected Pair<List<Message>,float[]> measureCalibrateStep(int stepId, float[] stepValues, float[] calibration) { 
		if (stepId == 1) return pair(calibrate1(validate(stepValues), calibration), calibration); 
		if (stepId == 2) return pair(calibrate2(validate(stepValues), calibration), calibration);
		return null;
	}
	protected float[] validate(float[] stepValues) {
		float ph1 = stepValues[0];
		boolean ok = ph1 > 0 && ph1 < 13;
		for (int i = 1; i < bufferTemps.length; i ++) ok |= stepValues[i] > stepValues[i-1] && stepValues[i]-ph1 < 0.2;
		if (!ok) throw error(log,20064,major,"pH values for the buffer are not be correct - must be in range 0-13, increasing and with max span of 0.2pH");
		return stepValues;
	}

	protected List<Message> calibrate1(float[] stepValues, float[] calibration) {
		return funcStorage(s -> {
			s.c1 = calibrationMeasure(s, new SiBuffer(listb(stepValues)));
			return validate(s.c1);
		});
	}
	protected List<Message> calibrate2(float[] stepValues, float[] calibration) { 
		return funcStorage(s -> {
			s.c2 = calibrationMeasure(s, new SiBuffer(listb(stepValues)));
			return concat(validate(s.c2),calibrateFinalize(s,calibration));
		});
	}
	protected List<Message> validate(CalibrationMeasure c) {
		if (c.eSpan > stableThreashold2)
			return msgs(20071,"The probe did not reach a stable measurement. You should probably re-do the calibration.");
		if (c.eSpan > stableThreashold1)
			return msgs(20072,
				"The probe did not reach a completely stable measurement. "+
				"That might be what you can expect for lower quality probes, but you might try to re-do the calibration.");
		return null;
	}

	protected List<Message> calibrateFinalize(S s, float[] calibration) {
		float x1 = x(s.c1.t,s.c1.buffer);
		float x2 = x(s.c2.t,s.c2.buffer);
		float slope = (s.c2.e - s.c1.e) / (x2 - x1);
		float e0 = s.c1.e + slope*x1;
		calibration[sIdx()] = slope;
		calibration[e0Idx()] = e0;
		log.info("calibrate: e0="+e0+"mV s="+slope+"mV/pH (x1="+x1+" x2="+x2+")");
		return validate(slope,e0);
	}
	private List<Message> validate(float slope, float e0) {
		List<Message> msgs = msgs();
		float sPct = sPct(slope);
		msgs.add(msg(20065,"The calibration resulted in a slope of {0}% and and offset of {1}mV.",sPct,e0));
		if (abs(e0) < 20 && sPct > 0.9 && sPct < 1.05) {
			msgs.add(msg(20066,"Your probe seems to be clean and in good health."));
		} else {
			if (abs(e0) > 40)
				msgs.add(msg(20067,
					"The offset is very bad (abs(e0) > 40mV), and your probe may broken. Try to clean it and then re-calibrate. "+
					"If this does not help, you should probably change the probe."));
			else if (abs(e0) > 20)
				msgs.add(msg(20068,"The offset is acceptable (abs(e0) > 20), but not optimal. Try to clean the probe and then re-calibrate."));
			if (sPct < 0.8 || sPct > 1.05)
				msgs.add(msg(20069,
						"The slope is very bad (<80% or > 105%), and your probe may be too old. Try to re-calibrate, "+
						"and if this does not help, you should probably change the probe."));
			else if (sPct < 0.9)
				msgs.add(msg(20070,
						"The slope is acceptable (<90%), but not optimal, and your probe will probably need to be replaced in the not to distant future. "+
						"You can keep using it for now, but be prepared that it will need to be replaced later."));
		}
		return msgs;
	}
	protected CalibrationMeasure calibrationMeasure(S s, Buffer buffer) {
		CalibrationMeasure c = new CalibrationMeasure();
		c.buffer = buffer;
		float prevVolt[] = new float[5];
		float prevPh[] = new float[prevVolt.length]; 
		for (int i = 0; i < prevVolt.length; i ++) { prevVolt[i] = i-99999999; prevPh[i] = i-99999999; }
		float minPh = 0, maxPh = 0, sumVolt = 0;

		for (int n = 0; n < maxTimesToMeasureForStableCalibration; n++) {
			measureAndStoreSingle(s);
			s.pH = pHExpected(s.pHVolt,s.temp);
			s.listeners.values().stream().forEach(l -> l.accept(measureFromStore(s)));
			
			for (int i = 1; i < prevVolt.length; i ++) { prevVolt[i-1] = prevVolt[i]; prevPh[i-1] = prevPh[i]; }
			prevVolt[prevVolt.length] = s.pHVolt; prevPh[prevVolt.length] = s.pH;
			
			minPh = maxPh = prevPh[0];
			sumVolt = 0;
			int nRising = 0;
			for (int i = 1; i < prevPh.length; i ++) {
				float pH = prevPh[i];
				sumVolt += prevVolt[i];
				if (pH < minPh) minPh = pH;
				if (pH > maxPh) maxPh = pH;
				float prev = prevPh[i-1];
				if (pH > prev) nRising++; 
			}
			
			if ((n > maxTimesToMeasureForStableCalibration/2 || nRising < prevPh.length-1) && maxPh-minPh < stableThreashold1) break; 
			reallySleep(milliesBetweenMeasureForStableCalibration);
		}
		forceMeasure();
		c.t = s.temp;
		c.e = sumVolt/prevVolt.length/1000;
		c.eSpan = maxPh - minPh;
		log.info("calibrate c: t="+c.t+"°C e="+c.e+"mV ("+minPh+"-"+maxPh+"pH)");
		return c;
	}
	
	public static float tempK(float tempC) { return tempC + 273.15f; }
	public static float x(float tempC, Buffer buffer) { return rByF * tempK(tempC) * (buffer.apply(tempC) - pHi); }
	public static float pH(float phVolt, float tempC, float e0, float s) { return (float) ((e0 - phVolt/1000) / (s * rByF * tempK(tempC)) + pHi); }
	public static float pHExpected(float phVolt, float tempC) { return pH(phVolt,tempC,0,maxSlope*0.98f); }
	public static float sPct(float s) { return s/maxSlope; }
}
