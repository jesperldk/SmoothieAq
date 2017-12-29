package jesperl.dk.smoothieaq.server.driver.abstracts;

import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.util.shared.error.Severity.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.util.*;
import java.util.function.*;

import jesperl.dk.smoothieaq.server.access.abstracts.*;
import jesperl.dk.smoothieaq.server.access.classes.*;
import jesperl.dk.smoothieaq.server.driver.classes.*;
import jesperl.dk.smoothieaq.util.shared.error.*;
import jesperl.dk.smoothieaq.util.shared.*;

public abstract class  AbstractDriver<S extends AbstractDriver.Storage, D extends DeviceAccess> implements Driver {
	
	public static class  Storage {
		public float[] calibration;
		public void initCalibration(int size) { calibration = new float[size]; }
	}

	public static class  StepInfoField {
		public Message fieldInfo;
		public float defaultValue;
		public StepInfoField(Message fieldInfo, float defaultValue) { 
			this.fieldInfo = fieldInfo; this.defaultValue = defaultValue; 
		}
	}
	public static class  StepInfo {
		public int stepId;
		public Message stepInfo;
		public StepInfoField[] fields;
		public StepInfo(int stepId, int noFields, Message stepInfo) { this(stepId, new StepInfoField[noFields], stepInfo); }
		public StepInfo(int stepId, StepInfoField[] fields, Message stepInfo) { 
			this.stepId = stepId; this.stepInfo = stepInfo; this.fields = fields; 
		}
	}
	
//	private DeviceAccessContext context;
	private D da;
	private S storage;
	private boolean simulate;
	
//	protected final ErrorHandler eh = new ErrorHandler() {
//		@Override public ErrorException apply(ErrorException ee) { context.er(da, ee); return ee; }
//	};

	@Override public abstract void init(DeviceAccessContext context, String urlString, float[] calibration);

	@Override public abstract Message description();
	@Override public abstract Message name();
	@Override public abstract List<String> getDefaultUrls(DeviceAccessContext context);
	
	@SuppressWarnings({ "unchecked" })
	protected void init(DeviceAccessContext context, String urlString, Class<D> daType, Class<? extends AbstractDriver<?,?>> storageKey, Supplier<S> newStorage, float[] calibration) {
		assert this.da == null : "double call of init";
		assert context != null && urlString != null && storageKey != null;
		this.simulate = context.isSimulate();
		da = (D) context.get(urlString);
		if (!daType.isAssignableFrom(da.getClass())) throw error(20021,major,"You must use an url with bus that implements {0} interfacet",AbstractDeviceAccess.bus(daType));
		useDeviceAccess(da -> { 
			storage = (S) da.retrieve(storageKey);
			if (storage == null) { 
				storage = newStorage.get();
				storage.calibration = new float[calibrationUse()];
				if (calibration == null)
					initCalibration(storage.calibration);
				else
					for (int i = 0; i < calibration.length; i++)
						if (i < storage.calibration.length) storage.calibration[i] = calibration[i];
				da.store(storageKey, storage);
			}
		});
	}
	
	@Override public DeviceUrl getUrl() { return da.getUrl(); }
	
	protected void initCalibration(float[] calibration) {};
	protected int calibrationUse() { return 0; }
	
	protected void useDeviceAccess(Consumer<D> user) { assert da != null; synchronized(da) { user.accept(da); } }
	protected <T> T funcDeviceAccess(Function<D,T> func) { assert da != null; synchronized(da) { return func.apply(da); } }

	protected void useStorage(Consumer<S> user) { assert storage != null; useDeviceAccess(da -> user.accept(storage)); }
	protected <T> T  funcStorage(Function<S,T> func) { assert storage != null; return funcDeviceAccess(da -> func.apply(storage)); }
	
	protected boolean isSimulate() { return simulate; }
	
	@Override public void release() { da.release(); da = null; }
	
	@Override public StepInfo[] calibrationInfo() { return null; }
	@Override public int daysBetweenCalibration() { return -1; }
	@Override public float[] startCalibration() { return funcStorage(s -> s.calibration); }
	@Override public Pair<List<Message>,float[]> calibrateStep(int stepId, float[] stepValues, float[] calibration) { return pair(null, calibration); }
	@Override public float[] finalizeCalibration(float[] calibration) { 
		useStorage(s -> s.calibration = calibration);
		return calibration;
	}
}
