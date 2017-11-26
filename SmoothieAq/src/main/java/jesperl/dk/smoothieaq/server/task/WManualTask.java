package jesperl.dk.smoothieaq.server.task;

import java.time.*;

import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.server.task.classes.*;
import jesperl.dk.smoothieaq.shared.model.schedule.*;
import jesperl.dk.smoothieaq.shared.model.task.*;

public class  WManualTask extends WTask implements ManualTask {
	
	@Override public synchronized void start(State state) {
		internalSetManualState(state,state.now.millis(),0);
		// TODO message
		notifyScheduled(state);
	}
	@Override public void end(State state) {}

	@Override public synchronized void done(State state, TaskArg arg, String description) {
		Instant now = state.now.instant();
		next = new Interval(now, now);
		done(state, false, arg, description);
		internalSetManualState(state,0,0);
		scheduleChanged(state);
	}

	@Override public synchronized void skip(State state) {
		Instant now = state.now.instant();
		next = new Interval(now, now);
		done(state, true, null, null);
		internalSetManualState(state,0,0);
		scheduleChanged(state);
	}

	@Override public synchronized void postpone(State state, long postponeTo) {
		internalSetManualState(state,0,postponeTo);
		scheduleChanged(state);
	}

	protected void internalSetManualState(State state, long manualWaitingFrom, long manualPostponedTo) {
		TaskStatus status = cloneStatus();
		status.manualWaitingFrom = manualWaitingFrom;
		status.manualPostponedTo = manualPostponedTo;
		internalSet(state, status);
	}
}
