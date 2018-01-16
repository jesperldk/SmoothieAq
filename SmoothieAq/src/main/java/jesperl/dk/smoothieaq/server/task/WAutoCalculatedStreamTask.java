package jesperl.dk.smoothieaq.server.task;

import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.shared.model.schedule.*;

public class  WAutoCalculatedStreamTask extends WAutoTask {

	@Override protected void autoDo(State state) {}
	@Override protected void autoStart(State state, Interval next) { device.setValue(-9999); }
	@Override protected void autoEnd(State state) { device.setValue(-9999); }
}
