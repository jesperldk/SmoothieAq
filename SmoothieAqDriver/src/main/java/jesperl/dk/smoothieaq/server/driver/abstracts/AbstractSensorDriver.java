package jesperl.dk.smoothieaq.server.driver.abstracts;

import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.util.*;
import java.util.function.*;

import jesperl.dk.smoothieaq.server.access.classes.*;
import jesperl.dk.smoothieaq.server.driver.classes.*;
import jesperl.dk.smoothieaq.util.shared.error.*;
import jesperl.dk.smoothieaq.util.shared.*;

public abstract class  AbstractSensorDriver<S extends AbstractSensorDriver.Storage, D extends DeviceAccess> extends AbstractDriver<S,D> implements SensorDriver {
	
	public static final long measureValidMillis = 3 * 1000;

	public static class  Storage extends AbstractDriver.Storage {
		public long stamp = 0;
		public Map<Class<?>, Consumer<Float>> listeners = new HashMap<>();
	}
	
	private Supplier<Float> simulator = defaultSimulator();
	
	protected Supplier<Float> defaultSimulator() { return new Simulator(55.5f, 3.0f, 6.0f, 20); }
	@Override
	public void setSimulator(Supplier<Float> simulator) { this.simulator = simulator; }
	protected Supplier<Float> simulator() { return simulator; }
	
	@Override protected int calibrationUse() { return super.calibrationUse() + (shiftFields() == null ? 0 : shiftFields().size()); }
	
	@Override
	public float measure() {
		return funcStorage(s -> {
			if (isSimulate()) return simulator.get();
			if (s.stamp == 0 || s.stamp + measureValidMillis() < System.currentTimeMillis()) {
				s.stamp = System.currentTimeMillis(); 
				measureAndStore(s);
				s.listeners.values().stream().forEach(l -> l.accept(measureFromStore(s)));
			}
			return measureFromStore(s);
		});
	}
	
	protected abstract float measureFromStore(S storage);
	
	protected abstract void measureAndStore(S storage);
	
	protected long measureValidMillis() { return measureValidMillis; }
	
	@Override
	public void forceMeasure() { useStorage(s -> s.stamp = 0); }
	
	@Override
	public void listen(Consumer<Float> listener) { useStorage(s -> s.listeners.put(AbstractSensorDriver.this.getClass(), listener)); }
	
	@Override public void release() {
		super.release();
		useStorage(s -> s.listeners.remove(this.getClass()));
	}

	@Override public StepInfo[] calibrationInfo() {
		StepInfo[] infos = measureCalibrationInfo();
		List<String> shiftFields = shiftFields();
		if (shiftFields == null) return infos;
		StepInfo info = new StepInfo(999, shiftFields.size(), 
			(infos.length == 0) ?
				msg(20040, "This device driver does not support proper calibration, it does however allow you to shift the measured values with a constant. Usually you should <i>not</i> do that ;-).")
			:
				msg(20041, "In addition to the proper calibration, you can shift the measured values with a constant. Usually you should <i><b>never</b></i> do that ;-).")
		);
		useStorage(s -> {
			if (shiftFields.size() == 1 && infos.length == 0)
				info.fields[0] = new StepInfoField(msg(20042, "Value to shift all measures with"), s.calibration[0]);
			else
				for (int i = 0; i < shiftFields.size(); i++)
					info.fields[i] = new StepInfoField(msg(20043, "Value to shift the measure of {0} with",shiftFields.get(i)), s.calibration[i]);
		});
		return concat(infos,array(info)); 
	}
	protected StepInfo[] measureCalibrationInfo() { return null; }
	protected List<String> shiftFields() { return null; }
	
	@Override public Pair<List<Message>,float[]> calibrateStep(int stepId, float[] stepValues, float[] calibration) { 
		if (shiftFields() == null || stepId != 999) return measureCalibrateStep(stepId, stepValues, calibration);
		for (int i = 0; i < shiftFields().size(); i++) calibration[i] = stepValues[i];
		return pair(null,calibration);
	}
	protected Pair<List<Message>,float[]> measureCalibrateStep(int stepId, float[] stepValues, float[] calibration) { return pair(null,calibration); }
}
