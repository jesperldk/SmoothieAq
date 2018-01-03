package jesperl.dk.smoothieaq.client.context;

public class CContext {
	public static final CContext ctx = new CContext();
	
	public final CWires cWires = new CWires();
	public final CDrivers cDrivers = new CDrivers();
	public final CDevices cDevices = new CDevices();

	public void init() {
		cWires.init();
	}
}
