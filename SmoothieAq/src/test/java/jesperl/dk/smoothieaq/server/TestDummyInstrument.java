package jesperl.dk.smoothieaq.server;

public class  TestDummyInstrument extends Test {

	public static void main(String[] args) {

//		now.flyTo(i(2,22,15,10));
//		println("it is now "+now.instant());
//
//		int  componentTypeId = state.dContext.addDriverForTest(new DummyInstrument()).getId();
//		
////		Instrument instrumentType = state.get(Instrument.class ,componentTypeId);
//		Device inst = new Device();
//		inst.driverId = componentTypeId;
//		inst.dependencyType = DeviceDependencyType.none;
//		inst.deviceType = DeviceType.sensor;
//		inst.deviceClass = DeviceClass.sensor;
//		int instId = state.save(inst).getId();
////		inst.setDescription(state, instrumentType.getDescription());
////		inst.resetDefaultTasks(state);
//		IDevice instw = new WDevice(inst);
//		instw.init(state.dContext);
//		
//		Task task = Task.create(instId,TaskType.autoMeasure,null,array(EveryNMinutes.create(false, 5))); 
//		instw.addTask(state, task);
//
////		println("tasks: "+inst.getTasks());
////		inst.unpause(state);
//		
//		SchedulerContext context = new SchedulerContext(state);
//		
//		task = instw.getTasks().iterator().next();
//		for (int i = 0; i < 20; i++) {
//			println("it is now "+now.instant());
//			Instant nextStart = task.next(context).b.start();
//			println("shedule for "+nextStart);
//			now.flyTo(nextStart);
//			do { try {
//				Thread.sleep(1000);
//			} catch (Exception e) {} } while (false);
//			println("it is now "+now.instant());
//			Instant last = context.instant();
//			context.setLast(task.getId(), new Interval(last, last));
//			println(" measured: "+((InstrumentX)instw.getDriver()).measure(state, instw.getInstance(),InstrumentX.MeasureType.normal).value);
//		}
//		println("done");
	}

}
