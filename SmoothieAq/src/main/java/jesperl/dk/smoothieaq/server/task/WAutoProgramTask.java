package jesperl.dk.smoothieaq.server.task;

import static java.time.temporal.ChronoUnit.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.util.concurrent.atomic.*;

import jesperl.dk.smoothieaq.server.device.classes.*;
import jesperl.dk.smoothieaq.server.driver.classes.*;
import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.shared.model.schedule.*;
import jesperl.dk.smoothieaq.shared.model.task.*;

public class  WAutoProgramTask extends WAutoTask {
	
	@Override protected void autoDo(State state) {}
	@Override protected void autoStart(State state, Interval next) { 
		((LevelDevice)device).level(delayedMinutes(state, next),createProgram(next,(ProgramTaskArg)task.taskArg)); 
	}
	@Override protected void autoEnd(State state) { device.setValue(0); }

	private int delayedMinutes(State state, Interval next) { 
		return Math.max(0,(int) next.a.until(state.now.instant(),MINUTES)); 
	}
	private LevelProgram createProgram(Interval next, ProgramTaskArg taskArg) {
		StepProgram program = new StepProgram();
		
		AtomicInteger argDuration = new AtomicInteger(0);
		forEach(taskArg.startDuration, d -> argDuration.addAndGet(d));
		forEach(taskArg.endDuration, d -> argDuration.addAndGet(d));
		int plen = taskArg.startDuration.length + taskArg.endDuration.length + 1;
		
		program.stepDurationMinutes = new int[plen];
		AtomicInteger p = new AtomicInteger(0);
		forEach(taskArg.startDuration, d -> program.stepDurationMinutes[p.getAndIncrement()] = d);
		program.stepDurationMinutes[p.getAndIncrement()] = (int) (next.a.until(next.b, MINUTES) - argDuration.get());
		forEach(taskArg.endDuration, d -> program.stepDurationMinutes[p.getAndIncrement()] = d);

		program.stepEndLevel = new float[plen];
		p.set(0);
		forEach(taskArg.startLevel, l -> program.stepEndLevel[p.getAndIncrement()] = (float) l);
		program.stepEndLevel[p.getAndIncrement()] = taskArg.startLevel[taskArg.startLevel.length-1];
		forEach(taskArg.endLevel, l -> program.stepEndLevel[p.getAndIncrement()] = (float) l);
		
		program.algorithm = 1;
		return program;
	}
}
