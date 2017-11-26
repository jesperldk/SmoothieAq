package jesperl.dk.smoothieaq.server;

import java.time.*;

import jesperl.dk.smoothieaq.server.scheduler.*;
import jesperl.dk.smoothieaq.shared.model.schedule.*;
import jesperl.dk.smoothieaq.shared.model.task.*;

public class  TestSchedule2 extends Test {

	public static void main(String[] args) {
		
		Instant i = i(2,19,14,0);
		state.now.flyTo(i);
		state.now.setTimeIsFlying(true);
		
		SchedulerContext context = new SchedulerContext(state);
		Scheduler scheduler = new Scheduler(context);
		
		int task1id;
		{
			Schedule schedules = EveryNDays.create(false, 2, ScheduleTime.create(11, 30));
			Task task = Task.create(0, TaskType.manualDosing, null, schedules);
			task1id = state.save(task).getId();
//			scheduler.addToSchedule(task,(d,c) -> {}, (d,c) -> {});
		}
		
		int task2id;
		{
			Schedule schedules = IntervalStartLength.create(PointAtDayAbsolute.create(ScheduleTime.create(22, 15), null),ScheduleTime.create(6, 15));
			Task task = Task.create(0, TaskType.autoOnoff, null, schedules);
			task2id = state.save(task).getId();
//			scheduler.addToSchedule(task,(d,c) -> {}, (d,c) -> {});
		}
		
		{
			Schedule schedules = IntervalStartLength.create(PointEqualTo.create(task1id),ScheduleTime.create(0, 15));
			Task task = Task.create(0, TaskType.autoOnoff, null, schedules);
			state.save(task);
//			scheduler.addToSchedule(task,(d,c) -> {}, (d,c) -> {});
		}
		
		{
			Schedule schedules = IntervalStartLength.create(PointEqualTo.create(task2id),ScheduleTime.create(0, 15));
			Task task = Task.create(0, TaskType.autoOnoff, null, schedules);
			state.save(task);
//			scheduler.addToSchedule(task,(d,c) -> {}, (d,c) -> {});
		}
		
		scheduler.run(20);
	}

}
