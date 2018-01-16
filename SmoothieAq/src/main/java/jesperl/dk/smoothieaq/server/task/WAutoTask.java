package jesperl.dk.smoothieaq.server.task;

import java.time.*;
import java.util.*;

import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.server.task.classes.*;
import jesperl.dk.smoothieaq.shared.model.schedule.*;
import rx.*;

public abstract class  WAutoTask extends WTask implements ITask {
	
	protected List<Subscription> subscriptions = null;
	
	@Override public synchronized void start(State state) {
		Instant now = state.now.instant();
		if (task.taskType.info().intervalSchedule) {
			on = true;
			autoStart(state, next);
			if (sApply != null) wire(state);
			next = new Interval(now, next.b);
		} else {
			next = new Interval(now,now);
			autoDo(state);
			done(state, false, null, null);
		}
	}
	@Override public synchronized void end(State state) {
		Instant now = state.now.instant();
		next = new Interval(next.a,now);
		if (subscriptions != null) unwire(state);
		autoEnd(state);
		done(state, false, null, null);
	}
	
	protected void wire(State state) {
		subscriptions = new ArrayList<>();
		sApply.wire(state.dContext, device.drain(), subscriptions);
	}
	protected void unwire(State state) {
		for (int i = subscriptions.size(); i > 0; i--) subscriptions.get(i-1).unsubscribe();
		subscriptions = null;
	}

	protected abstract void autoDo(State state);
	protected abstract void autoStart(State state, Interval next);
	protected abstract void autoEnd(State state);
	
}
