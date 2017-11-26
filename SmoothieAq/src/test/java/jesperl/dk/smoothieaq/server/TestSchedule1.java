package jesperl.dk.smoothieaq.server;

import java.time.*;

import jesperl.dk.smoothieaq.server.scheduler.*;
import jesperl.dk.smoothieaq.shared.model.schedule.*;
import jesperl.dk.smoothieaq.shared.model.task.*;

public class TestSchedule1 extends Test {

	public static void main(String[] args) {
		
		Instant i = i(2,19,14,0);
		state.now.flyTo(i);
		Task task = Task.create(0, null);
		state.save(task);
		
		state.now.flyTo(i(2,20,20,11));
		SchedulerContext schedulerContext = new SchedulerContext(state);
//		schedulerContext.setLast(task.getId(), new Interval(i, i)); TODO
		TaskContext context = schedulerContext.taskContext(task.getId());

		EveryNDays everyNDays = EveryNDays.create(false, 2, ScheduleTime.create(11, 30));
		System.out.println(p(everyNDays.next(context)));

		EveryNHours everyNHours = EveryNHours.create(false, 2, 27);
		System.out.println(p(everyNHours.next(context)));

	}

}
