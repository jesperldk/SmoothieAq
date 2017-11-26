package jesperl.dk.smoothieaq.server.driver.classes;

import java.util.function.*;

public interface SensorDriver extends Driver {

	void setSimulator(Supplier<Float> simulator);

	float measure();

	void forceMeasure();

	void listen(Consumer<Float> listener);

}