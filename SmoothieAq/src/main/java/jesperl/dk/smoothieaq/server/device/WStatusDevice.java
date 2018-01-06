package jesperl.dk.smoothieaq.server.device;

import static jesperl.dk.smoothieaq.shared.util.Objects2.*;

import jesperl.dk.smoothieaq.server.device.classes.*;
import jesperl.dk.smoothieaq.server.driver.classes.*;
import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;
import rx.*;

public class  WStatusDevice extends WDevice<StatusDriver> implements StatusDevice {
	
	private int blinkGrade = 0; // our current state, 0=off, 1=on, >1=blink

	@Override public void setValue(float value) { blink((int) value); }
	@Override public float getValue() { return getBlink(); } 

	@Override public void off() { blink(0); }
	@Override public void blink(int grade) { if (grade != blinkGrade) deviceBlink(grade); }

	@Override public boolean isOn() { return getBlink() > 0; }
	@Override public int getBlink() { return blinkGrade; }

	@Override protected void stop(State state) {
		deviceBlink(0);
		super.stop(state);
	}
	@Override protected void setupStreams(State state) {
		addStream(state, DeviceStream.onoff,MeasurementType.onoff,() -> baseStream());
		addStream(state, DeviceStream.startstopX,MeasurementType.onoff, () -> stream);
		addStream(state, DeviceStream.level,MeasurementType.status, () -> baseStream());
		addStream(state, DeviceStream.watt,MeasurementType.energyConsumption, () -> only(0f));
		addStream(state, DeviceStream.pauseX,MeasurementType.status,() -> Observable.just(0f).concatWith(pauseStream));
		super.setupStreams(state);
	}
	private Observer<Float> level() { return isPaused() ? pauseStream : stream; }

	protected void deviceBlink(int grade) {
		doErrorGuarded(() -> {
			if (grade == 0) driver().off();
			else driver().blink(grade);
			blinkGrade = grade;
			level().onNext((float) grade);
		});
	}

}
