package jesperl.dk.smoothieaq.server.driver.classes;

import java.util.function.*;

public interface ToggleDriver extends Driver {

	boolean isOn();

	void listenOn(Consumer<Boolean> listener);

}