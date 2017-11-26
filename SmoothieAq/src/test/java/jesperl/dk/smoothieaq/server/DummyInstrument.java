package jesperl.dk.smoothieaq.server;

import jesperl.dk.smoothieaq.server.device.x.*;
import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;

public class DummyInstrument extends AbstractInstrumentX {

	public DummyInstrument() {
		super("Dummy Instrument", MeasurementType.temperature, 0.1);
	}

	
	@Override
	public void resetDefaultTasks(State state, Device component) {
		super.resetDefaultTasks(state, component);
		
//		AutoMeasureTask measureTask = new AutoMeasureTask(state.getNextId(), (Instrument)component, new EveryNMinutes(false, 5)); 
//		// TODO save
//		
//		component.addTask(state, measureTask);
	}


	@Override
	public Measure measure(State state, Device instance, MeasureType measureType) {
//		return MeasurementType.temperature.measurement(state, getId(),24.5f);
		return null;
	}

}
