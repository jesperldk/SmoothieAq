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

	@Override public void setValue(float value) {}
	@Override public float getValue() { return measure(); } 

	@Override public float measure() { return deviceMeasure(); }

	@Override protected void enable(State state) { 
		enabled = true;
		super.enable(state); 
		startstopX.onNext(1f); 
	}
	@Override protected void disable(State state) { 
		enabled = false;
		startstopX.onNext(0f); 
		stream.onNext(disabledLevel); 
		super.disable(state);
	}
	@Override protected void pause(State state) {
		enabled = true;
		super.pause(state);
		startstopX.onNext(1f); 
	}
	@Override protected void setupStreams(State state) {
		super.setupStreams(state);
		addDefaultStream(DeviceStream.level,device.measurementType,() -> baseStream());
		addStream(DeviceStream.measureX,device.measurementType,() -> stream);
		addStream(DeviceStream.onoff,MeasurementType.onoff,() -> Observable.just(enabled ? 1f : 0f).concatWith(startstopX));
		addStream(DeviceStream.startstopX,MeasurementType.onoff, () -> startstopX);
		addStream(DeviceStream.watt,MeasurementType.energyConsumption, () -> only(0f));
		subscribeMeasure(state,DeviceStream.level);
		setupBaseStreams(state);
	}
	@Override protected void setupPauseStreams(State state) {
		super.setupPauseStreams(state);
//		driver().listen(f -> calibrationX.onNext(f)); // TODO only when calibrating
		addStream(DeviceStream.pauseX,device.measurementType,() -> baseStream());
		subscribeOtherMeasure(state,DeviceStream.pauseX);
		setupBaseStreams(state);
	}
	protected void setupBaseStreams(State state) {
		subscription(state.wires.pulse.onBackpressureDrop()
				.observeOn(Schedulers.io()) // some measures can take seconds
				.delay((getId()%10)*100, TimeUnit.MILLISECONDS) // don't hit the device busses at the same time
				.subscribe(v -> deviceMeasure())); // for the side effect
	}

	protected float deviceMeasure() {
		if (!enabled) return disabledLevel;
		doErrorGuarded(() -> { 
			nextLevel = driver().measure(); 
			if (abs(prevLevel-nextLevel) > device.repeatabilityLevel) {
				stream.onNext(nextLevel);
				prevLevel = nextLevel;
			}
		});
		return nextLevel;
	}

}
