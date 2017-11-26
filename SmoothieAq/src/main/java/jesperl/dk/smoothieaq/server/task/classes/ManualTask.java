package jesperl.dk.smoothieaq.server.task.classes;

import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.shared.model.task.*;

public interface ManualTask extends ITask {

	void done(State state, TaskArg arg, String description);
	void skip(State state);
	void postpone(State state, long postponeTo);
}
