package jesperl.dk.smoothieaq.server.device.classes;

import jesperl.dk.smoothieaq.server.driver.classes.*;

public interface LevelDevice extends IDevice {
	void on(float level);
	void level(int startAtMinutes, LevelProgram program);
	void off();
	boolean isOn();
	float getLevel();
}
