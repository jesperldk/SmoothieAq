package jesperl.dk.smoothieaq.server.device;

import jesperl.dk.smoothieaq.server.device.classes.*;
import jesperl.dk.smoothieaq.server.driver.classes.*;
import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;

public class  WOnoffDevice extends WDevice<OnoffDriver> implements OnoffDevice {
	
	private boolean on;
	
	@Override public void setValue(float value) { if (value <= 0) off(); else on(); }
	@Override public float getValue() { return isOn() ? 1 : 0; } 

	@Override public void on() { if (!on) onoff(true); }
	@Override public void off() { if (on) onoff(false); }

	@Override public boolean isOn() { return on; }

	@Override protected void stop(State state) {
		onoff(false);
		super.stop(state);
	}
	@Override protected void setupStreams(State state) {
		super.setupStreams(state);
		addDefaultStream(DeviceStream.onoff,MeasurementType.onoff,() -> baseStream());
		addStream(DeviceStream.level,device.measurementType, () -> baseStream().map(v -> v*device.onLevel));
		subscribeMeasure(state,DeviceStream.level);
		setupBaseStreams(state);
	}
	@Override protected void setupPauseStreams(State state) {
		super.setupPauseStreams(state);
		addStream(DeviceStream.pauseX,device.measurementType, () -> baseStream().map(v -> v*device.onLevel));
		subscribeOtherMeasure(state,DeviceStream.level);
		setupBaseStreams(state);
	}
	protected void setupBaseStreams(State state) {
		addStream(DeviceStream.startstopX,MeasurementType.onoff, () -> stream);
		addStream(DeviceStream.watt,MeasurementType.energyConsumption, () -> baseStream().map(v -> v*device.wattAt100pct));
		subscribeMeasure(state,DeviceStream.watt);
		subscribeOtherMeasure(state,DeviceStream.startstopX);
	}

	protected void onoff(boolean on) {
		doErrorGuarded(() -> {
			driver().onoff(on);
			this.on = on;
			stream.onNext(on ? 1f : 0f);
		});
	}
	
}
