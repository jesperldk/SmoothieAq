package jesperl.dk.smoothieaq.server.scheduler;

import java.time.*;
import java.time.temporal.*;

import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.shared.model.schedule.*;

public class OffsetTaskContext extends TaskContext {
	
	private NowWithOffset now;
	private Interval last;

	protected OffsetTaskContext(TaskContext parrent, Interval next) {
		this(parrent, next.end());
		last = next;
	}
	
	protected OffsetTaskContext(TaskContext parrent, Temporal flyTo) {
		super(parrent);
		now = new NowWithOffset();
		now.flyTo(flyTo);
	}
	
	@Override public Interval last() { return last; }
	
	@Override public Interval last(int taskId) {
		if (taskId == getTask().getId()) return last;
		return super.last(taskId);
	}
	
	@Override public Instant instant() { return now.instant(); }

	@Override public LocalDateTime localDateTime() { return now.localDateTime(); }
}
