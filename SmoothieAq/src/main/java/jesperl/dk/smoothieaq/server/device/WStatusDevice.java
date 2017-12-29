package jesperl.dk.smoothieaq.server.device;

import static jesperl.dk.smoothieaq.shared.util.Objects2.*;

import jesperl.dk.smoothieaq.server.device.classes.*;
import jesperl.dk.smoothieaq.server.driver.classes.*;
import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;

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
		super.setupStreams(state);
		addDefaultStream(DeviceStream.onoff,MeasurementType.onoff,() -> baseStream());
		addStream(DeviceStream.startstopX,MeasurementType.onoff, () -> stream);
		addStream(DeviceStream.level,MeasurementType.status, () -> baseStream());
		addStream(DeviceStream.watt,MeasurementType.energyConsumption, () -> only(0f));
		subscribeMeasure(state,DeviceStream.level);
	}
	@Override
	protected void setupPauseStreams(State state) {
		super.setupPauseStreams(state);
		addStream(DeviceStream.pauseX,MeasurementType.onoff,() -> baseStream());
		subscribeOtherMeasure(state,DeviceStream.pauseX);
	}

	protected void deviceBlink(int grade) {
		doErrorGuarded(() -> {
			if (grade == 0) driver().off();
			else driver().blink(grade);
			blinkGrade = grade;
			stream.onNext((float) grade);
		});
	}

}
