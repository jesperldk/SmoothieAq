package jesperl.dk.smoothieaq.server.device;

import static jesperl.dk.smoothieaq.shared.model.device.DeviceStream.*;

import java.util.concurrent.*;

import jesperl.dk.smoothieaq.server.device.classes.*;
import jesperl.dk.smoothieaq.server.driver.classes.*;
import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;
import rx.*;
import rx.schedulers.*;
import rx.subjects.*;

public class  WDoserDevice extends WDevice<DoserDriver> implements DoserDevice {
	
	protected PublishSubject<Float> doseX = PublishSubject.create();
	protected PublishSubject<Float> soFar = PublishSubject.create();
	Subscription pulseSubscr;
	private long start = 0;
	private long end = 0;

	@Override public void setValue(float value) { if (value == 0) off(); else dose(value); }
	@Override public float getValue() { return isOn() ? 1 : 0; } 

	@Override public void on() { if (!isOn()) deviceOn(); }
	@Override public void off() { if (isOn()) deviceOff(); }
	@Override public void dose(float amount) { deviceDose(amount); }

	@Override public boolean isOn() { return soFar() > 0; }
	@Override public float soFar() { return deviceSoFar(); }

	@Override protected void stop(State state) {
		super.stop(state);
		deviceOff();
	}
	@Override protected void setupStreams(State state) {
		super.setupStreams(state);
		addDefaultStream(amountX,device.measurementType,() -> doseX);
		subscribeMeasure(state,amountX);
		setupBaseStreams(state);
	}
	@Override protected void setupPauseStreams(State state) {
		super.setupPauseStreams(state);
		setupBaseStreams(state);
		addStream(pauseX,device.measurementType,() -> doseX);
		subscribeOtherMeasure(state,pauseX);
	}
	protected void setupBaseStreams(State state) {
		addStream(startstopX,MeasurementType.onoff, () -> stream);
		addStream(sofar,device.measurementType, () -> Observable.just(0f).concatWith(soFar));
		addStream(watt,MeasurementType.energyConsumption, () -> baseStream().map(v -> v*device.wattAt100pct));
		subscribeMeasure(state,watt);
		subscribeOtherMeasure(state,startstopX);
		subscribeOtherMeasure(state,sofar);
	}
	@Override protected void teardownStreams() {
		if (pulseSubscr != null) pulseSubscr.unsubscribe();
		super.teardownStreams();
	}

	protected void deviceOn() {
		doErrorGuarded(() -> {
			driver().onoff(true);
			start = System.currentTimeMillis();
			end = Long.MAX_VALUE;
			startPulse();
			stream.onNext(1f);
		});
	}
	protected void deviceOff() {
		doErrorGuarded(() -> {
			long end = System.currentTimeMillis();
			driver().onoff(false);
			doseX.onNext((end-start)/1000f*driver().getAmountPerSec());
			start = 0;
			stream.onNext(0f);
		});
	}
	protected void deviceDose(float amount) {
		doErrorGuarded(() -> {
			driver().dose(amount);
			start = System.currentTimeMillis();
			end = start + (long)(amount/driver().getAmountPerSec()*1000);
			startPulse();
			stream.onNext(1f);
		});
	}
	protected float deviceSoFar() {
		if (start == 0) return 0;
		if (System.currentTimeMillis() > end) {
			doseX.onNext((end-start)/1000f*driver().getAmountPerSec());
			start = 0;
			stream.onNext(0f);
			return 0;
		}
		float amountSoFar = (System.currentTimeMillis()-start)/1000f*driver().getAmountPerSec();
		soFar.onNext(amountSoFar);
		return amountSoFar;
	}
	protected void startPulse() {
		pulseSubscr = 
			Observable.interval(100, 500, TimeUnit.MILLISECONDS, Schedulers.computation())
				.subscribe(l -> { if (deviceSoFar() == 0) pulseSubscr.unsubscribe(); }); // we call deviceSoFar for the side effect
	}

}
