package jesperl.dk.smoothieaq.server.device;

import static jesperl.dk.smoothieaq.shared.util.Objects2.*;

import jesperl.dk.smoothieaq.server.device.classes.*;
import jesperl.dk.smoothieaq.server.driver.classes.*;
import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;

public class  WToggleDevice extends WDevice<ToggleDriver> implements ToggleDevice {
	
	public static final float disabledLevel = -999999;
	
	private boolean disabled = true; 
	private boolean on = false;

	@Override public void setValue(float value) {}
	@Override public float getValue() { return disabled ? disabledLevel : on ? 1f : 0f; } 

	@Override public boolean isOn() { return on; }

	@Override protected void enable(State state) {  
		disabled = false; 
		super.enable(state);
		deviceIsOn();
		stream.onNext(getValue()); 
	}
	@Override protected void disable(State state) { 
		disabled = true; 
		stream.onNext(disabledLevel); 
		super.disable(state); 
	}
	@Override protected void pause(State state) {  
		disabled = false; 
		stream.onNext(disabledLevel); 
		super.enable(state);
		deviceIsOn();
		stream.onNext(getValue()); 
	}
	@Override protected void unpause(State state) { 
		disabled = true; 
		super.disable(state); 
		stream.onNext(getValue()); 
	}
	@Override protected void setupStreams(State state) {
		super.setupStreams(state);
		addDefaultStream(DeviceStream.onoff,MeasurementType.onoff,() -> baseStream());
		addStream(DeviceStream.level,MeasurementType.onoff,() -> baseStream());
		addStream(DeviceStream.startstopX,MeasurementType.onoff, () -> stream);
		addStream(DeviceStream.watt,MeasurementType.energyConsumption, () -> only(0f));
		subscribeMeasure(state,DeviceStream.onoff);
		setupBaseMeasure(); 
	}
	@Override protected void setupPauseStreams(State state) {
		super.setupStreams(state);
		addStream(DeviceStream.pauseX,MeasurementType.onoff,() -> baseStream());
		subscribeOtherMeasure(state,DeviceStream.pauseX);
		setupBaseMeasure(); 
	}
	protected void setupBaseMeasure() {
		driver().listenOn(b -> { if (!disabled) { on = b; stream.onNext(on ? 1f : 0f); }});
	}
	@Override protected void teardownStreams() {
		super.teardownStreams();
		driver().listenOn(null); 
	}

	protected boolean deviceIsOn() {
		doErrorGuarded(() -> { on = driver().isOn(); });
		return on;
	}

}
