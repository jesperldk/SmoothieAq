package jesperl.dk.smoothieaq.server.device.classes;

public interface OnoffDevice extends IDevice {
	void on();
	void off();
	boolean isOn();
}
