package jesperl.dk.smoothieaq.server.device;

import static jesperl.dk.smoothieaq.shared.util.Objects2.*;

import jesperl.dk.smoothieaq.server.device.classes.*;
import jesperl.dk.smoothieaq.server.driver.classes.*;
import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;
import rx.*;

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
		super.pause(state);
		deviceIsOn();
		pauseStream.onNext(getValue()); 
	}
	@Override protected void unpause(State state) { 
		disabled = true; 
		super.disable(state); 
		stream.onNext(getValue()); 
	}
	@Override protected void setupStreams(State state) {
		addStream(state, DeviceStream.onoff,MeasurementType.onoff,() -> baseStream());
		addStream(state, DeviceStream.level,MeasurementType.onoff,() -> baseStream());
		addStream(state, DeviceStream.startstopX,MeasurementType.onoff, () -> stream);
		addStream(state, DeviceStream.watt,MeasurementType.energyConsumption, () -> only(0f));
		addStream(state, DeviceStream.pauseX,MeasurementType.onoff,() -> Observable.just(0f).concatWith(pauseStream));
		driver().listenOn(b -> { if (!disabled) { on = b; onoff().onNext(on ? 1f : 0f); }});
		super.setupStreams(state);
	}
	private Observer<Float> onoff() { return isPaused() ? pauseStream : stream; }
	@Override protected void teardownStreams() {
		super.teardownStreams();
		driver().listenOn(null); 
	}

	protected boolean deviceIsOn() {
		doErrorGuarded(() -> { on = driver().isOn(); });
		return on;
	}

}
