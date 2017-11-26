#ifndef DOING_H_
#define DOING_H_

//#include <SmoothieAq.h>

#include <Device.h>

unsigned const char maxSteps = 12;

class Device;

class Doing {
private:
	Device* device;
	unsigned long start = 0;
	unsigned long end;
	unsigned short steps[maxSteps];
	unsigned char noSteps;
	unsigned char doingStep;

public:

	bool isFree();

	void startDoing(Device* device);

	void setEnd(unsigned long durationMillis);

	void stopDoing();

	void endDoing();

	void errorEndDoing(String message);

	bool startDoingSteps();

	unsigned char currentStepNo();

	unsigned int currentStep();

	unsigned int nextStep();

	void skipStep();

	unsigned char noOfSteps();

	unsigned long howFar();

	void doing();
};

#endif /* DOING_H_ */
