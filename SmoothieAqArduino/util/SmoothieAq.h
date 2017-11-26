/*
 * SmoothieAq.h
 *
 *  Created on: 9. apr. 2017
 *      Author: jesper
 */

#ifndef SMOOTHIEAQ_H_
#define SMOOTHIEAQ_H_

#include <CmdMessenger.h>
#include <TimerOne.h>
#include <Device.h>

extern const String name;
extern const String ver;
const String SmoothieAqName = "SmoothieAq";
const String SmoothieAqVer = "0.1"; // increase major (only!) with protocol changes, increase minor with any change

enum Cmds {
	versionCmd = 1,     // -> versionReply
	timeCmd = 2,        // -> timeReply
	buzzCmd = 3, 		// buzz-millies [, pause-millies, buzz-millies ]... -> okReply
	onCmd = 4,          // logical-no -> okReply
	offCmd = 5,         // logical-no -> okReply
	doCmd = 6,          // logical-no, seconds -> okReply
	valueCmd = 7,       // logical-no -> valueReply
	levelCmd = 8,       // logical-no, start-at {, minutes, level }... -> okReply; (level must be 1-999)
	statusCmd = 9,    	// logical-no -> onReply | offReply | doReply | levelReply
	confCmd = 10,       // -> confReply
	blinkCmd = 11,      // on-millies [, off-millies, on-millies ]... -> okReply

	okReply = 100,      //
	errorReply = 199,   // error-message
	versionReply = 101, // "SmoothieAq", smoothieAqArduinoVer, sketch-name, sketch-ver
	timeReply = 102,    // hh,mm,ss
	valueReply = 107,   // logical-no, value
	onReply = 159,      // logical-no
	offReply = 169,     // logical-no
	doReply = 179,      // logical-no, done-seconds, total-seconds
	levelReply = 189,   // cur-level, done-minutes, total-minutes
	confReply = 110,    // { CmdType, DevType, l }...

	setupInternal = 200,
	doInternal = 201,
	doStepInternal = 202,
	doEndInternal = 203,
};

const unsigned char maxDoings = 8;

class SmoothieAq {

private:
	CmdMessenger cmdMessenger = CmdMessenger(Serial);

protected:

	static void onCommand();

	static void onLogical();

	static void onUnknown();

	static void onVersion();

	static void onBuzz();

	static void onBlink();

	static void onConf();

public:

	Doing doings[maxDoings];
	BuzzDevice* buzzDevice = nullptr;
	StatusDevice* statusDevice = nullptr;
	bool timer1initialized = false;

	CmdMessenger* msg();

	void error(String msg);

	void ok();

	void blink();

	void buzz();

	void setupSmoothieAq();

	void loopSmoothieAq();
};

extern SmoothieAq saq;

#endif /* SMOOTHIEAQ_H_ */
