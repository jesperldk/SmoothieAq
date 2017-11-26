package jesperl.dk.smoothieaq.server.driver.abstracts;

import java.util.logging.*;

import jesperl.dk.smoothieaq.server.access.classes.*;
import jesperl.dk.smoothieaq.server.driver.classes.*;

public abstract class AbstractOnoffDriver<S extends AbstractDriver.Storage, D extends DeviceAccess> extends AbstractDriver<S,D> implements OnoffDriver {
	private final static Logger log = Logger.getLogger(AbstractOnoffDriver.class.getName());

	@Override
	public void onoff(boolean on) {
		if (!isSimulate()) {
			if (on) on();
			else off();
		}
		log.info("Turned "+(on?"on":"off")+" ("+getUrl()+")");
	}
	
	protected abstract void on();
	protected abstract void off();
}
