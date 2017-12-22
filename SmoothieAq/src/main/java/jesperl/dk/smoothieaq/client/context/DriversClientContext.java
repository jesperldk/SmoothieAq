package jesperl.dk.smoothieaq.client.context;

import static jesperl.dk.smoothieaq.shared.util.Objects2.stream;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import jesperl.dk.smoothieaq.client.*;
import jesperl.dk.smoothieaq.client.components.GuiUtil.*;
import jesperl.dk.smoothieaq.shared.resources.DeviceRest.*;
import rx.*;

public class DriversClientContext {
	
	private Observable<DriverView> drivers;
	private WOptions<Short> options;

	public Observable<DriverView> getDrivers() {
		if (drivers == null) drivers = Resources.device.drivers().cache();
		return drivers;
	}
	
	public Observable<DriverView> getDriver(short id) {
		return getDrivers().first(d -> d.driverId == id);
	}
	
	public WOptions<Short> getOptions() {
		if (options == null) options = new WOptions<Short>(stream(getDrivers().map(d -> {
			return pair(new Short(d.driverId), d.name.format());
		})));
		return options;
	}

}
