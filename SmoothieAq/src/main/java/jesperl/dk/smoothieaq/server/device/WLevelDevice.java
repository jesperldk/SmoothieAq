package jesperl.dk.smoothieaq.server.device;

import static java.lang.Math.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import jesperl.dk.smoothieaq.server.device.classes.*;
import jesperl.dk.smoothieaq.server.driver.classes.*;
import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;
import rx.subjects.*;

public class  WLevelDevice extends WDevice<LevelDriver> implements LevelDevice {
	
	protected PublishSubject<Float> startstopX = PublishSubject.create();
	private float prevLevel = 0;
	private Float level = null; // null if no current fixed level
	private LevelProgram program = null; // null if no current program
	private long programStart;
	private long programEnd;

	@Override public void setValue(float value) { on(value); }
	@Override public float getValue() { return getLevel(); } 

	@Override public void on(float level) { if (this.level == null || level != this.level) deviceLevel(level); }
	@Override public void level(int startAtMinutes, LevelProgram program) {	deviceProgram(startAtMinutes,program); }
	@Override public void off() { on(0); }

	@Override public boolean isOn() { return getLevel() > 0; }
	@Override public float getLevel() {	return nnv(level,() -> programLevel()); }

	@Override protected void stop(State state) {
		off();
		super.stop(state);
	}
	@Override protected void setupStreams(State state) {
		super.setupStreams(state);
		addDefaultStream(DeviceStream.level,device.measurementType,() -> baseStream());
		addStream(DeviceStream.pctlevel,MeasurementType.change, () -> baseStream().map(v -> v/driver().getMaxLevel()));
		addStream(DeviceStream.onoff,MeasurementType.onoff,() -> baseStream().map(v -> v == 0f ? 0f : 1f));
		subscribeMeasure(state,DeviceStream.level);
		setupBaseStreams(state);
	}
	@Override protected void setupPauseStreams(State state) {
		super.setupPauseStreams(state);
		addDefaultStream(DeviceStream.pauseX,device.measurementType,() -> baseStream());
		subscribeOtherMeasure(state,DeviceStream.pauseX);
		setupBaseStreams(state);
	}
	protected void setupBaseStreams(State state) {
		addStream(DeviceStream.startstopX,MeasurementType.onoff, () -> startstopX);
		addStream(DeviceStream.watt,MeasurementType.energyConsumption, () -> baseStream().map(v -> v/driver().getMaxLevel()*device.wattAt100pct));
		subscribeMeasure(state,DeviceStream.watt);
		subscribeOtherMeasure(state,DeviceStream.startstopX);
		subscription(state.wires.pulse.onBackpressureDrop().subscribe(v -> programLevel())); // for the side effect 
	}

	synchronized protected void deviceLevel(float level) {
		doErrorGuarded(() -> {
			if (level == 0) driver().off();
			else if (level > 0) driver().on(level);
			program = null;
			this.level = level == 0 ? null : level;
			stream.onNext(level);
			startstopX.onNext(level == 0 ? 0f : 1f);
		});
	}
	synchronized protected void deviceProgram(int startAtMinutes, LevelProgram program) {
		doErrorGuarded(() -> {
			driver().level(startAtMinutes, program);
			programStart = System.currentTimeMillis();
			programEnd = programStart + program.duration()*60*1000;
			level = null;
			prevLevel = 0;
			this.program = program;
			stream.onNext(0f);
			startstopX.onNext(1f);
		});
	}
	synchronized protected float programLevel() {
		if (program == null) return 0;
		if (System.currentTimeMillis() > programEnd) {
			program = null;
			stream.onNext(0f);
			startstopX.onNext(0f);
			return 0;
		}
		float nextLevel = program.at((int) ((System.currentTimeMillis()-programStart)/1000/60));
		if (abs(prevLevel-nextLevel) > device.repeatabilityLevel) {
			stream.onNext(nextLevel);
			prevLevel = nextLevel;
		}
		return nextLevel;
	}
	
}
