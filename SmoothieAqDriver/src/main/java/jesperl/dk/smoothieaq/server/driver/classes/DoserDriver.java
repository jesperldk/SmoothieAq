package jesperl.dk.smoothieaq.server.driver.classes;

public interface DoserDriver extends Driver {

	void onoff(boolean on);
	void dose(float amount);
	float getAmountPerSec();
}