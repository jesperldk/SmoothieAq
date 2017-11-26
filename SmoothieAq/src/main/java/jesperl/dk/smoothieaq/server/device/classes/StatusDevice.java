package jesperl.dk.smoothieaq.server.device.classes;

public interface StatusDevice extends IDevice {
	void off();
	void blink(int grade);
	boolean isOn();
	int getBlink();
}
