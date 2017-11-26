package jesperl.dk.smoothieaq.server.driver;

import static jesperl.dk.smoothieaq.shared.error.Errors.*;

import java.util.function.*;

import jesperl.dk.smoothieaq.server.driver.classes.*;
import jesperl.dk.smoothieaq.shared.error.*;

public class CN0326TempDriver extends CN0326Driver {

	@Override public float measureFromStore(Storage s) { return s.temp; }
	@Override public Message name() { return msg(20203,"CN0326 thermometer"); }
	@Override protected Supplier<Float> defaultSimulator() { return new Simulator(25f, 0.3f, -2f, 4); }
}
