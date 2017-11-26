package jesperl.dk.smoothieaq.server.device;

import static jesperl.dk.smoothieaq.shared.util.Objects2.*;

import jesperl.dk.smoothieaq.server.device.classes.*;
import jesperl.dk.smoothieaq.server.driver.classes.*;
import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;

public class WToggleDevice extends WDevice<ToggleDriver> implements ToggleDevice {
	
	public static final float disabledLevel = -999999;
	
	private boolean disabled = true; 
	private boolean on = false;

	@Override public void setValue(float value) {}
	@Override public float getValue() { return disabled ? disabledLevel : on ? 1f : 0f; } 

	@Override public boolean isOn() { return on; }

	@Override protected void getready(DeviceContext dContext) { super.getready(dContext); }
	@Override protected void start(State state) { 
		disabled = false; 
		on = false;
		subscribeMeasure(state,DeviceStream.onoff);
		stream.onNext(0f); 
	}
	@Override protected void stop(State state) { disabled = true; stream.onNext(disabledLevel); super.stop(state); }

	protected boolean deviceIsOn() {
		doErrorGuarded(() -> { on = driver().isOn(); });
		return on;
	}

	@Override protected void setupStreams() {
		super.setupStreams();
		driver().listenOn(b -> { if (!disabled) { on = b; stream.onNext(on ? 1f : 0f); }}); 
		addDefaultStream(DeviceStream.onoff,MeasurementType.onoff,() -> baseStream());
		addStream(DeviceStream.level,MeasurementType.onoff,() -> baseStream());
		addStream(DeviceStream.startstopX,MeasurementType.onoff, () -> stream);
		addStream(DeviceStream.watt,MeasurementType.energyConsumption, () -> only(0f));
	}
}
