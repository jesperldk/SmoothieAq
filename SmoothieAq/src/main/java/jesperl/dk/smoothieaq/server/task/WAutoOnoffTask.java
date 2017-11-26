package jesperl.dk.smoothieaq.server.task;

import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.shared.model.schedule.*;

public class  WAutoOnoffTask extends WAutoTask {

	@Override protected void autoDo(State state) {}
	@Override protected void autoStart(State state, Interval next) { device.setValue(sApply == null ? 1 : 0); }
	@Override protected void autoEnd(State state) { device.setValue(0); }
}
