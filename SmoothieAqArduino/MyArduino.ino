#include <TimerOne.h>
#include <CmdMessenger.h>
#include <SmoothieAq.h>
#include <Device.h>
#include <Doing.h>

extern const String name = "light"; // unique describing name among all your Smoothie Aq Arduino devices, use names like "plug", "doser", "light", etc
extern const String ver = "0.1";

// set up the devs[] array according to the actual physical configuration of your Arduino based device
// the position in the array gives you the logical-no, starting with 1
// {CmdType, DevType, pin-no, SpecType}
//const unsigned char devs[][4] = { { onoffTyp, lightDev, 13, noSpec }, {
//		levelTyp, lightDev, 9, timerOnePWMSpec }, { noTyp, 0, 0, 0 }, // not used
//		{ buzzTyp, buzzDev, 2, noSpec }, };

LevelDevice light1 = LevelDevice(lightDev,10,timer1Opt,3);
//OnoffDevice light2 = OnoffDevice(lightDev,3);
NoDevice unused = NoDevice();
BuzzDevice buzz = BuzzDevice(2);
StatusDevice status = StatusDevice(13,invertOpt);

Device* devices[] = {&light1,&unused,&buzz,&status};

unsigned char maxLogicals = sizeof(devices) / sizeof(devices[0]);

SmoothieAq saq = SmoothieAq();

void setup() {
	Serial.begin(50000); // 50000 baud is Smoothie Aq standard, it is 16Mhz/320 :-)
	while (!Serial) {} // wait for serial port to connect. Needed for native USB port only

	saq.setupSmoothieAq();
}

void loop() {
	saq.loopSmoothieAq();
	delay(20);
}
