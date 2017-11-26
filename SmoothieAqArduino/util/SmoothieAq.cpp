/*
 * SmoothieAq.h
 *
 *  Created on: 9. apr. 2017
 *      Author: jesper
 */

#include <SmoothieAq.h>
#include <CmdMessenger.h>
#include <Device.h>

extern Device* devices[];
extern unsigned char maxLogicals;

CmdMessenger* SmoothieAq::msg() { return &cmdMessenger; }

void SmoothieAq::error(String msg) {
	saq.buzz(); saq.blink();
	cmdMessenger.sendCmd(errorReply, msg);
}

void SmoothieAq::ok() { cmdMessenger.sendCmd(okReply); }

void SmoothieAq::blink() { if (statusDevice != nullptr) statusDevice->blink(); }

void SmoothieAq::buzz() { if (buzzDevice != nullptr) buzzDevice->buzz(); }

void SmoothieAq::onCommand() {
	saq.blink();
}

void SmoothieAq::onLogical() {
	onCommand();
	int logical = saq.msg()->readInt16Arg() - 1;
	if (logical < 0 || logical >= maxLogicals) saq.error("unsupported logical-no for this command");
	else devices[logical]->handle(saq.msg()->commandID());
}

void SmoothieAq::onUnknown() {
	saq.error("unknown command");
}

void SmoothieAq::onVersion() {
	onCommand();
	saq.msg()->sendCmdStart(versionReply);
	saq.msg()->sendCmdArg(SmoothieAqName);
	saq.msg()->sendCmdArg(SmoothieAqVer);
	saq.msg()->sendCmdArg(name);
	saq.msg()->sendCmdArg(ver);
	saq.msg()->sendCmdEnd();
}

void SmoothieAq::onBuzz() {
	if (saq.buzzDevice == nullptr) saq.error("no buzzer");
	else saq.buzzDevice->handle(buzzCmd);
}

void SmoothieAq::onBlink() {
	if (saq.statusDevice == nullptr) saq.error("no status device");
	else saq.statusDevice->handle(blinkCmd);
}

void SmoothieAq::onConf() {
	onCommand();
	saq.msg()->sendCmdStart(confReply);
	for (unsigned char logical = 0; logical < maxLogicals; logical++) {
		saq.msg()->sendCmdArg(devices[logical]->devCls);
		saq.msg()->sendCmdArg(devices[logical]->devType);
	}
	saq.msg()->sendCmdEnd();
}

void SmoothieAq::setupSmoothieAq() {
	cmdMessenger.printLfCr();

	cmdMessenger.attach(onUnknown);
	cmdMessenger.attach(versionCmd, onVersion);
	cmdMessenger.attach(onCmd, onLogical);
	cmdMessenger.attach(offCmd, onLogical);
	cmdMessenger.attach(doCmd, onLogical);
	cmdMessenger.attach(statusCmd, onLogical);
	cmdMessenger.attach(levelCmd, onLogical);
	cmdMessenger.attach(buzzCmd, onBuzz);
	cmdMessenger.attach(blinkCmd, onBlink);
	cmdMessenger.attach(confCmd, onConf);

	for (unsigned char logical = 0; logical < maxLogicals; logical++) {
//		devices[logical]->setLogical(logical);
		devices[logical]->handle(setupInternal);
	}
	delay(1000); // let us make sure an Arduino Micro is ready
//	cmdMessenger.sendCmd(okReply, "tjuhej");
}

void SmoothieAq::loopSmoothieAq() {
	cmdMessenger.feedinSerialData();

	for (unsigned char logical = 0; logical < maxLogicals; logical++)
		devices[logical]->doing();
}


