package jesperl.dk.smoothieaq.server.device;

import static java.lang.Math.*;
import static jesperl.dk.smoothieaq.shared.util.Objects2.*;

import java.util.concurrent.*;

import jesperl.dk.smoothieaq.server.device.classes.*;
import jesperl.dk.smoothieaq.server.driver.classes.*;
import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;
import rx.*;
import rx.schedulers.*;
import rx.subjects.*;

public class  WSensorDevice extends WDevice<SensorDriver> implements SensorDevice {
	
	public static final float disabledLevel = -999999;
	
	protected PublishSubject<Float> startstopX = PublishSubject.create();
	protected PublishSubject<Float> calibrationX = PublishSubject.create();
	private boolean disabled = true; 
	private float prevLevel = disabledLevel;
	private float nextLevel = disabledLevel;

	@Override public void setValue(float value) {}
	@Override public float getValue() { return measure(); } 

	@Override public float measure() { return deviceMeasure(); }

	@Override protected void getready(DeviceContext dContext) { super.getready(dContext); }
	@Override protected void start(State state) { 
		disabled = false; 
		subscribeMeasure(state,DeviceStream.level);
		startstopX.onNext(1f); 
		subscription(state.wires.pulse.onBackpressureDrop()
				.delay(getId()%10, TimeUnit.SECONDS) // don't hit the device busses at the same time
				.observeOn(Schedulers.io()) // some measures can take seconds
				.subscribe(v -> deviceMeasure())); // for the side effect
	}
	@Override protected void stop(State state) { 
		disabled = true; 
		startstopX.onNext(0f); 
		stream.onNext(disabledLevel); 
		super.stop(state);
	}

	protected float deviceMeasure() {
		if (disabled) return disabledLevel;
		doErrorGuarded(() -> { 
			nextLevel = driver().measure(); 
			if (abs(prevLevel-nextLevel) > device.repeatabilityLevel) {
				stream.onNext(nextLevel);
				prevLevel = nextLevel;
			}
		});
		return nextLevel;
	}

	@Override protected void setupStreams() {
		super.setupStreams();
		driver().listen(f -> calibrationX.onNext(f)); // TODO only when calibrating
		addDefaultStream(DeviceStream.level,device.measurementType,() -> baseStream());
		addStream(DeviceStream.levelX,device.measurementType,() -> stream);
		addStream(DeviceStream.onoff,MeasurementType.onoff,() -> Observable.just(disabled ? 0f : 1f).concatWith(startstopX));
		addStream(DeviceStream.startstopX,MeasurementType.onoff, () -> startstopX);
		addStream(DeviceStream.watt,MeasurementType.energyConsumption, () -> only(0f));
	}
}
