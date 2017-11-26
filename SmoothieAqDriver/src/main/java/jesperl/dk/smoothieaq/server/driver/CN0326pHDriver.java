package jesperl.dk.smoothieaq.server.driver;

import static jesperl.dk.smoothieaq.shared.error.Errors.*;

import java.util.function.*;

import jesperl.dk.smoothieaq.server.driver.classes.*;
import jesperl.dk.smoothieaq.shared.error.*;

public class CN0326pHDriver extends CN0326Driver {

	@Override public float measureFromStore(Storage s) { return s.pH; }
	@Override public Message name() { return msg(20202,"CN0326 acidity (pH)"); }
	@Override protected Supplier<Float> defaultSimulator() { return new Simulator(6f, 0.01f, -1f, 2); }
}
