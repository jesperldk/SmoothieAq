package jesperl.dk.smoothieaq.server.task;

import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.shared.model.schedule.*;
import jesperl.dk.smoothieaq.shared.model.task.*;

public class  WAutoDoseAmountTask extends WAutoTask {

	@Override protected void autoDo(State state) { device.setValue(((LevelTaskArg)task.taskArg).level); }
	@Override protected void autoStart(State state, Interval next) {}
	@Override protected void autoEnd(State state) {}
}
