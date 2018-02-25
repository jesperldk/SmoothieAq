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
//	protected PublishSubject<Float> calibrationX = PublishSubject.create();
	private boolean enabled = false;
	private float prevLevel = disabledLevel;
	private float nextLevel = disabledLevel;

	@Override public void setValue(float value) { measure(); }
	@Override public float getValue() { return prevLevel; } 

	@Override public float measure() { return deviceMeasure(); }

	@Override protected void enable(State state) { 
		enabled = true;
		super.enable(state); 
		startstopX.onNext(1f); 
	}
	@Override protected void disable(State state) { 
		if (enabled) {
			startstopX.onNext(0f); 
			prevLevel = disabledLevel;
			level().onNext(disabledLevel); 
		}
		enabled = false;
		super.disable(state);
	}
	@Override protected void pause(State state) {
		if (enabled) {
			stream.onNext(disabledLevel); 
			startstopX.onNext(0f);
		}
		enabled = true;
		super.pause(state);
	}
	@Override protected void setupStreams(State state) {
		addStream(state, DeviceStream.level,device.measurementType,() -> baseStream());
		addStream(state, DeviceStream.measureX,device.measurementType,() -> stream);
		addStream(state, DeviceStream.onoff,MeasurementType.onoff,() -> Observable.just(enabled ? 1f : 0f).concatWith(startstopX));
		addStream(state, DeviceStream.startstopX,MeasurementType.onoff, () -> startstopX);
		addStream(state, DeviceStream.watt,MeasurementType.energyConsumption, () -> only(0f));
		addStream(state, DeviceStream.pauseX,device.measurementType,() -> Observable.just(disabledLevel).concatWith(pauseStream));
//		System.out.println("listen on pulse: "+getId());
		subscription(state.wires.pulse.onBackpressureDrop()
				.observeOn(Schedulers.io()) // some measures can take seconds
				.delay((getId()%10)*100, TimeUnit.MILLISECONDS) // don't hit the device busses at the same time
				.subscribe(v -> {/*System.out.println("pulse: "+getId())*/; deviceMeasure();})); // for the side effect
		super.setupStreams(state);
	}
	private Observer<Float> level() { return isPaused() ? pauseStream : stream; }

	protected float deviceMeasure() {
		if (!enabled) return disabledLevel;
		doErrorGuarded(() -> { 
			nextLevel = driver().measure(); 
			if (abs(prevLevel-nextLevel) > device.repeatabilityLevel) {
				level().onNext(nextLevel);
				prevLevel = nextLevel;
			}
		});
		return nextLevel;
	}

}
