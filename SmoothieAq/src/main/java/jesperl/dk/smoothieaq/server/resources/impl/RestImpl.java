package jesperl.dk.smoothieaq.server.resources.impl;

import jesperl.dk.smoothieaq.server.device.*;
import jesperl.dk.smoothieaq.server.device.classes.*;
import jesperl.dk.smoothieaq.server.state.*;
import jesperl.dk.smoothieaq.util.shared.error.Errors.*;

public class  RestImpl {
	
	public State state() { return State.state(); }

	protected DeviceContext context() { return state().dContext; }
	
	protected Wires wires() { return state().wires; }
	public void doGuarded(Doit doit) { state().wires.doGuarded(doit); }
	public <T> T funcGuarded(Supplyit<T> doit) { return state().wires.funcGuarded(doit); }
	
	protected IDevice idev(int id) { return context().getDevice(id); }
	
}
