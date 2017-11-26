/*
 * Device.h
 *
 *  Created on: 9. apr. 2017
 *      Author: jesper
 */

#ifndef DEVICE_H_
#define DEVICE_H_

//#include <SmoothieAq.h>
#include <Doing.h>

enum DevCls {
	emptyCls = 0,
	onoffCls = 1,
	levelCls = 2,
	measureCls = 2,
	bussCls = 10,
	statusCls = 11,
};

enum DevType {
	unspecDev = 0,
	doserDev = 1,
	lightDev = 2,
	fanDev = 3,
	tempDev = 4,
	out5vDev = 5,
	out12vDev = 6,
	measureDev = 7,
	buzzDev = 8,
	timeDev = 9,
	flagDev = 10,
	systemFlagDev = 11,
	systemTempDev = 12,
	plugDev = 13,
};

enum DevOpt {
	noOpt = 0,
	invertOpt = 1,
	timer1Opt = 2,
	noErrorBuzzOpt = 3,
};

class Doing;

class Device {
private:
	unsigned char pinNo;
	unsigned char flagPinNo;
//	unsigned char logical;

protected:
	int currentValue;
	Doing* currentDoing = nullptr;
	DevOpt devOpt;
//	bool invertFlag = false;

	Device(DevCls devCls, DevType devType, unsigned char pinNo, int devOpt=noOpt, unsigned char flagPinNo=99);

	bool startDoing();

	void stopDoing();

	bool startDoingSteps();

public:
	DevType devType;
	DevCls devCls;

	//	void setLogical(unsigned char logical);

//	void invert();

	bool isInvert();

//	unsigned char getLogical();

	unsigned char pin();

	void clearDoing(); // only to be called from Doing.endDoing()

	void doing();

	void setOnoff(boolean on);

	void flagOnoff(boolean on);

	void flagInit();

	virtual void handle(unsigned char cmd);
};

//////NO_DEVICE////////////////////////////////
class NoDevice: public Device {
public:
	NoDevice();
};

//////ONOFF_DEVICE////////////////////////////////
class OnoffDevice: public Device {
protected:

	void startDo();

public:
	OnoffDevice(DevCls devCls, DevType devType, unsigned char pinNo, int devOpt=noOpt, unsigned char flagPinNo=99);
	OnoffDevice(DevType devType, unsigned char pinNo, int devOpt=noOpt, unsigned char flagPinNo=99);

	virtual void handle(unsigned char cmd) override;
};

//////STATUS_DEVICE////////////////////////////////

class StatusDevice: public OnoffDevice {
protected:

	void startBlink();

public:
	void blink();
	StatusDevice(unsigned char pinNo, int devOpt=noOpt);

	virtual void handle(unsigned char cmd) override;
};

//////BUZZ_DEVICE////////////////////////////////

class BuzzDevice: public Device {
protected:

	void startBuzz();

public:
	void buzz();
	BuzzDevice(unsigned char pinNo, int devOpt=noOpt);

	virtual void handle(unsigned char cmd) override;
};

//////LEVEL_DEVICE////////////////////////////////

class LevelDevice: public OnoffDevice {
protected:
	int currentRealValue = 0;
	int prevStepValue;
	int targetStepValue;
	unsigned long stepDuration;
//	bool timerOnePWMFlag = false;
	bool inTimerOneMode = false;

	bool isTimer1();

	void startLevel();

	void doLevel();

	void stepLevel();

	void setLevel(int vaule);
	inline float sigmoid(float t);

	void simpleLevel(int value);

	void stopTimerOne();

public:
	LevelDevice(DevType devType, unsigned char pinNo, int devOpt=noOpt, unsigned char flagPinNo=99);

//	void timerOnePWM();

	virtual void handle(unsigned char cmd) override;
};


#endif /* DEVICE_H_ */
