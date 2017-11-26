#include <Arduino.h>
#include <SmoothieAq.h>
#include <Device.h>
#include <Doing.h>

	Device::Device(DevCls devCls, DevType devType, unsigned char pinNo, int devOpt=noOpt, unsigned char flagPinNo=99) {
		this->devCls = devCls;
		this->devType = devType;
		this->pinNo = pinNo;
		this->devOpt = devOpt;
		this->flagPinNo = flagPinNo;
	}

	bool Device::isInvert() { return (devOpt & invertOpt) > 0; }

	bool Device::startDoing() {
		stopDoing();
		unsigned char dNo = -1;
		while (++dNo < maxDoings)
			if (saq.doings[dNo].isFree()) { currentDoing = &saq.doings[dNo]; break; }
		if (currentDoing == nullptr) { saq.error("to many concurrent doings"); return false; }
		currentDoing->startDoing(this);
		return true;
	}

	void Device::stopDoing() { if (currentDoing != nullptr) currentDoing->stopDoing(); }

	bool Device::startDoingSteps() {
		if (!startDoing()) return false;
		return currentDoing->startDoingSteps();
	}

//	void Device::setLogical(unsigned char logical) { this->logical = logical; }
//
//	unsigned char Device::getLogical() { return logical; }

	unsigned char Device::pin() { return pinNo; }

	void Device::clearDoing() { currentDoing = nullptr; } // only to be called from Doing.endDoing()

	void Device::doing() { if (currentDoing != nullptr) currentDoing->doing(); }

	void Device::setOnoff(boolean on) {
		digitalWrite(pin(),isInvert() != on ? HIGH : LOW);
		currentValue = on ? 1 : 0;
		flagOnoff(on);
	}

	void Device::flagOnoff(boolean on) {
		if (flagPinNo != 99) {
			digitalWrite(flagPinNo,on ? HIGH : LOW);
		}
	}

	void Device::flagInit() { if (flagPinNo != 99) {
		pinMode(flagPinNo, OUTPUT); }
	}

	void Device::handle(unsigned char cmd) {
		if (cmd != setupInternal) saq.error("unsupported logical-no for this command");
	}

//////NO_DEVICE////////////////////////////////

	NoDevice::NoDevice() : Device(emptyCls, unspecDev, 0) {}

//////ONOFF_DEVICE////////////////////////////////

	void OnoffDevice::startDo() {
		int seconds = saq.msg()->readInt16Arg();
		if (seconds < 1 || seconds > 60 * 60) { saq.error("error in seconds"); return; }
		if (!startDoing()) return;
		currentDoing->setEnd(seconds * 1000);
		saq.ok();
	}

	OnoffDevice::OnoffDevice(DevType devType, unsigned char pinNo, int devOpt=noOpt, unsigned char flagPinNo=99) : OnoffDevice(onoffCls ,devType, pinNo, devOpt, flagPinNo) {}
	OnoffDevice::OnoffDevice(DevCls devCls, DevType devType, unsigned char pinNo, int devOpt=noOpt, unsigned char flagPinNo=99) : Device(devCls ,devType, pinNo, devOpt, flagPinNo) {}

	void OnoffDevice::handle(unsigned char cmd) {
		switch (cmd) {
		case setupInternal: pinMode(pin(), OUTPUT); flagInit(); setOnoff(false); return;
		case doInternal: if (currentValue == 0) setOnoff(true); return;
		case doEndInternal: setOnoff(false); return;
		case onCmd: stopDoing(); setOnoff(true); break;
		case offCmd: stopDoing(); setOnoff(false); break;
		case doCmd: startDo(); return;
		case statusCmd: saq.msg()->sendCmd(currentValue > 0 ? onReply : offReply); return; // TODO: status when doing
		default: saq.error("command not supported for this logical-no"); return;
		}
		saq.ok();
	}

//////BUZZ_DEVICE////////////////////////////////

	void BuzzDevice::buzz() {
		if ((devOpt & noErrorBuzzOpt) > 0) return;
		if (!startDoing()) return;
		currentDoing->setEnd(300);
	}

	void BuzzDevice::startBuzz() {
		if (!startDoingSteps()) return;
		currentDoing->setEnd(currentDoing->currentStep());
	}

	BuzzDevice::BuzzDevice(unsigned char pinNo, int devOpt=noOpt) : Device(bussCls, buzzDev ,devType, pinNo, devOpt) {}

	void BuzzDevice::handle(unsigned char cmd) {
		switch (cmd) {
		case setupInternal: pinMode(pin(), OUTPUT); setOnoff(false); saq.buzzDevice = this; return;
		case doInternal: if (currentDoing->currentStepNo() % 2 == 0) { saq.blink(); setOnoff(true); } else { setOnoff(false); } return;
		case doStepInternal: currentDoing->setEnd(currentDoing->currentStep()); return;
		case doEndInternal: setOnoff(false); return;
		case buzzCmd: startBuzz(); break;
		default: saq.error("command not supported for this logical-no"); return;
		}
		saq.ok();
	}

//////STATUS_DEVICE////////////////////////////////

	void StatusDevice::blink() {
		if (!startDoing()) return;
		currentDoing->setEnd(150);
	}

	void StatusDevice::startBlink() {
		if (!startDoingSteps()) return;
		currentDoing->setEnd(currentDoing->currentStep());
	}

	StatusDevice::StatusDevice(unsigned char pinNo, int devOpt=noOpt) : OnoffDevice(statusCls, systemFlagDev, pinNo, devOpt) {}

	void StatusDevice::handle(unsigned char cmd) {
		switch (cmd) {
		case setupInternal: pinMode(pin(), OUTPUT); setOnoff(false); saq.statusDevice = this; return;
		case doInternal: if (currentValue == 0) setOnoff(true); return;
		case doEndInternal: setOnoff(false); return;
		case blinkCmd: startBlink(); break;
		default: saq.error("command not supported for this logical-no"); return;
		}
		saq.ok();
	}

//////LEVEL_DEVICE////////////////////////////////

	bool LevelDevice::isTimer1() { return (devOpt & timer1Opt) > 0; }

	void LevelDevice::startLevel() {
		int startAt = saq.msg()->readInt16Arg();
		if (startAt < 0 || startAt > 24 * 60) { saq.error("error in start-at"); return; }
		if (!startDoingSteps()) return;
		if (currentDoing->noOfSteps()%2 == 1) { saq.error("wrong no of arguments"); return; }
		// TODO: handle startAt
		targetStepValue = 0;
		stepLevel();
		if (isTimer1()) {
			if (!saq.timer1initialized) Timer1.initialize(6666); // 150Hz PWM
			Timer1.pwm(pin(), 0);
			inTimerOneMode = true;
		}
		saq.ok();
	}

	void LevelDevice::doLevel() {
		if (false) { // linar
			setLevel(prevStepValue + ((targetStepValue - prevStepValue)*1.0)/stepDuration * currentDoing->howFar());
		} else { // s-curve
			float t = (currentDoing->howFar()*1.0)/(stepDuration*1.0)*14.0-7.0;
			setLevel(prevStepValue + (targetStepValue - prevStepValue) * sigmoid(t));
		}
	}

	const float e = exp(1);
	inline float LevelDevice::sigmoid(float t) { return 1.0/(1.0+pow(e,-t)); } // https://en.wikipedia.org/wiki/Sigmoid_function

	void LevelDevice::setLevel(int value) {
		if (value == currentValue) return;
		if (inTimerOneMode) {
			int pwm = (value*1.0)/1000*1023;
			if (pwm > 1023) pwm = 1023;
			if (pwm != currentRealValue) {
//Serial.print(" ");Serial.print(pwm); delay(200);
				Timer1.setPwmDuty(pin(),pwm);
				currentRealValue = pwm;
			}
		} else {
			int pwm = (value*1.0)/1000*255;
			if (pwm > 255) pwm = 255;
			if (pwm != currentRealValue) {
				analogWrite(pin(),pwm);
				currentRealValue = pwm;
			}
		}
		currentValue = value;
	}

	void LevelDevice::stepLevel() {
		prevStepValue = targetStepValue;
		stepDuration = currentDoing->currentStep();
		stepDuration *= 60;
		stepDuration *= 1000;
//stepDuration /= 4;
		currentDoing->skipStep();
		targetStepValue = currentDoing->currentStep();
		if (targetStepValue > 1000) targetStepValue = 1000;
		if (prevStepValue == targetStepValue) setLevel(targetStepValue);
		currentDoing->setEnd(stepDuration);
//Serial.print("\nprev ");Serial.println(prevStepValue);
//Serial.print("targ ");Serial.println(targetStepValue);
//Serial.print("dura ");Serial.println(stepDuration);
	}

	void LevelDevice::simpleLevel(int value) {
		targetStepValue = value;
		setLevel(targetStepValue);
	}

	LevelDevice::LevelDevice(DevType devType, unsigned char pinNo, int devOpt=noOpt, unsigned char flagPinNo=99) : OnoffDevice(levelCls ,devType, pinNo, devOpt, flagPinNo) {}

	void LevelDevice::stopTimerOne() { if (inTimerOneMode) { Timer1.disablePwm(pin()); inTimerOneMode = false; currentRealValue = 0; } }

	void LevelDevice::handle(unsigned char cmd) {
		switch (cmd) {
		case setupInternal: pinMode(pin(), OUTPUT); flagInit(); setOnoff(false); return;
		case doInternal: if (currentValue != targetStepValue) doLevel(); return;
		case doStepInternal: stepLevel(); return;
		case doEndInternal:  stopTimerOne(); setOnoff(false); return;
		case onCmd: stopDoing(); stopTimerOne(); flagOnoff(true); simpleLevel(500); break;
		case offCmd: stopDoing(); stopTimerOne(); setOnoff(false); break;
		case doCmd: startDo(); flagOnoff(true); simpleLevel(500); return;
		case levelCmd: startLevel(); flagOnoff(true); return;
		case statusCmd: saq.msg()->sendCmd(currentValue > 0 ? onReply : offReply); return; // TODO: status when doing
		default: saq.error("command not supported for this logical-no"); return;
		}
		saq.ok();
	}
