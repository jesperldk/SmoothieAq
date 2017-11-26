package jesperl.dk.smoothieaq.server.driver;

import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;

import java.util.function.*;

import jesperl.dk.smoothieaq.server.driver.classes.*;
import jesperl.dk.smoothieaq.util.shared.error.*;

public class  AM2321TempDriver extends AM2321Driver {

	@Override public float measureFromStore(Storage s) { return s.temp; }
	@Override public Message name() { return msg(20120,"AM2321 thermometer"); }
	@Override protected Supplier<Float> defaultSimulator() { return new Simulator(24f, 0.5f, 5f, 3); }
}
