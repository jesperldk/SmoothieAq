package jesperl.dk.smoothieaq.server.driver.classes;

public interface LevelDriver extends Driver {

	void level(int startAtMinutes, LevelProgram program);
	void on(float level);
	void off();
	float getMaxLevel();
}