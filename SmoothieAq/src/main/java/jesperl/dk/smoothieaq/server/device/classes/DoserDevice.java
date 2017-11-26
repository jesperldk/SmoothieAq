package jesperl.dk.smoothieaq.server.device.classes;

public interface DoserDevice extends IDevice {
	void on();
	void off();
	void dose(float amount);
	boolean isOn();
	float soFar();
}
