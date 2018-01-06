package jesperl.dk.smoothieaq.server.device;

import jesperl.dk.smoothieaq.server.device.classes.*;
import jesperl.dk.smoothieaq.server.driver.classes.*;
import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;
import rx.*;

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
		addStream(state, DeviceStream.onoff,MeasurementType.onoff,() -> baseStream());
		addStream(state, DeviceStream.level,device.measurementType, () -> baseStream().map(v -> v*device.onLevel));
		addStream(state, DeviceStream.startstopX,MeasurementType.onoff, () -> stream);
		addStream(state, DeviceStream.watt,MeasurementType.energyConsumption, () -> baseStream().map(v -> v*device.wattAt100pct));
		addStream(state, DeviceStream.pauseX, device.measurementType, () -> Observable.just(getValue()).concatWith(pauseStream).map(v -> v*device.onLevel));
		super.setupStreams(state);
	}
	private Observer<Float> onoff() { return isPaused() ? pauseStream : stream; }

	protected void onoff(boolean on) {
		doErrorGuarded(() -> {
			driver().onoff(on);
			this.on = on;
			onoff().onNext(on ? 1f : 0f);
		});
	}
	
}
