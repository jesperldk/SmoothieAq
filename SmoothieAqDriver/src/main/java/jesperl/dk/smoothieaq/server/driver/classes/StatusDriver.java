package jesperl.dk.smoothieaq.server.driver.classes;

public interface StatusDriver extends Driver {

	void blink(int level);
	void off();

}