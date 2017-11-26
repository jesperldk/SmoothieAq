package jesperl.dk.smoothieaq.server.task;

import jesperl.dk.smoothieaq.server.device.classes.*;
import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.shared.model.schedule.*;

public class  WAutoMeasureTask extends WAutoTask {

	@Override protected void autoDo(State state) { ((SensorDevice)device).measure(); }
	@Override protected void autoStart(State state, Interval next) {}
	@Override protected void autoEnd(State state) {}
}
