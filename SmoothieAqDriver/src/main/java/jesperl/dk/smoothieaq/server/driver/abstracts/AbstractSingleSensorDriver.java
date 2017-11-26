package jesperl.dk.smoothieaq.server.driver.abstracts;

import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.util.*;
import java.util.logging.*;

import jesperl.dk.smoothieaq.server.access.classes.*;

public abstract class  AbstractSingleSensorDriver<D extends DeviceAccess> extends AbstractSensorDriver<AbstractSingleSensorDriver.Storage,D> {
	private final static Logger log = Logger.getLogger(AbstractSingleSensorDriver.class .getName());
	
	public static class  Storage extends AbstractSensorDriver.Storage {
		public float measure;
	}

	@Override protected float measureFromStore(Storage s) { return s.measure; }
	@Override protected void measureAndStore(Storage s) { 
		s.measure = justMeasure() + s.calibration[0]; 
		log.fine("measured "+s.measure);
	}
	protected abstract float justMeasure();
	
	@Override protected List<String> shiftFields() { return list("field"); }
	
}
