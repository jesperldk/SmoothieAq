#include <Arduino.h>
#include <SmoothieAq.h>
#include <Doing.h>

	bool Doing::isFree() { return device == nullptr; }

	void Doing::startDoing(Device* device) {
		this->device = device;
		start = millis();
		doingStep = 0;
		noSteps = 0;
	}

	void Doing::setEnd(unsigned long durationMillis) { end = start + durationMillis; }

	void Doing::stopDoing() { device->handle(doEndInternal); endDoing(); }

	void Doing::endDoing() { start = 0; device->clearDoing(); device = nullptr; }

	void Doing::errorEndDoing(String message) { endDoing(); saq.error(message); }

	bool Doing::startDoingSteps() {
		noSteps = 0;
		while (true) {
			int step = saq.msg()->readInt16Arg();
			if (!saq.msg()->isArgOk()) break;
			if (noSteps >= maxSteps) { errorEndDoing("to many steps"); return false; }
			if (step < 0 || step > 2000) { errorEndDoing("error in arg"); return false; }
			steps[noSteps++] = step;
		}
		if (noSteps == 0) { errorEndDoing("not enough arguments"); return false; }
		return true;
	}

	unsigned char Doing::currentStepNo() { return doingStep; }

	unsigned int Doing::currentStep() { return steps[doingStep]; }

	void Doing::skipStep() { doingStep++; }

	unsigned char Doing::noOfSteps() { return noSteps; }

	unsigned long Doing::howFar() { return millis()-start; }

	void Doing::doing() {
		if (millis() <= end) { device->handle(doInternal); }
		else if (++doingStep < noSteps) { start = millis(); device->handle(doStepInternal); }
		else stopDoing();
	}
