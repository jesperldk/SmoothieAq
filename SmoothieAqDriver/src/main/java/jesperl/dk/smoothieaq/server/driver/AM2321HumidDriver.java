package jesperl.dk.smoothieaq.server.driver;

import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;

import java.util.function.*;

import jesperl.dk.smoothieaq.server.driver.classes.*;
import jesperl.dk.smoothieaq.util.shared.error.*;

public class  AM2321HumidDriver extends AM2321Driver {

	@Override public float measureFromStore(Storage s) { return s.humid; }
	@Override public Message name() { return msg(20110,"AM2321 humidity sensor"); }
	@Override protected Supplier<Float> defaultSimulator() { return new Simulator(35f, 1f, 25f, 5); }
}
