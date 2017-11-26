package jesperl.dk.smoothieaq.server.resources;

import jesperl.dk.smoothieaq.server.device.*;
import jesperl.dk.smoothieaq.server.device.classes.*;
import jesperl.dk.smoothieaq.server.state.*;

public class RestImpl {
	
	public State state() { return State.state(); }

	protected DeviceContext context() { return state().dContext; }
	
	protected IDevice idev(int id) { return context().getDevice(id); }
	
}
