package jesperl.dk.smoothieaq.server.access;

import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.util.shared.error.Severity.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.util.function.*;
import java.util.logging.*;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.*;

import jesperl.dk.smoothieaq.server.access.abstracts.*;
import jesperl.dk.smoothieaq.server.access.classes.*;

public class  PiDeviceAccess extends AbstractDeviceAccess implements GpioDeviceAccess {
	private final static Logger log = Logger.getLogger(PiDeviceAccess.class .getName());
	
	public static String bus = bus(PiDeviceAccess.class );
	
	private static GpioController controller;

	private int pinNo;
	private int mode;
	private int resistor;
	private Pin pin;
	private GpioPin gpioPin; 
	
	// URL: pi://gpio/pin:mode.resistor
	@Override public GpioDeviceAccess init(DeviceAccessContext context, DeviceUrl url) {
		super.init(context, url);
		doGuardedX(eh,() -> {
			if (!"gpio".equals(url.busSelector.toLowerCase())) throw new RuntimeException();
			pinNo = intv(url.deviceSelector);
			pin = RaspiPin.getPinByAddress(pinNo);
			mode = (url.deviceArgs.length > 0) ? intv(url.deviceArgs[0]) : 1;
			resistor = (url.deviceArgs.length > 1) ? intv(url.deviceArgs[1]) : 0;
			synchronized (PiDeviceAccess.class ) { if (controller == null) controller = GpioFactory.getInstance(); }
		}, e -> error(log,e,10500, major, "Malformed pi url: {0}",url.urlString));
		return this;
	}
	
	@Override protected void openIt() {
		setModeDigital(mode, resistor);;
		log.info("Opened "+getUrl().urlString);
		super.openIt();
	}
	
	@Override protected void closeIt() {
		controller.unprovisionPin(gpioPin);
		super.closeIt();
	}
	
	@Override public void setModeDigital(int mode, int resistor) {
		if (mode < 1 || mode > 3 || resistor < 0 || resistor > 2) throw error(log,10501,major,"Invalid arguments in url {0}",getUrl());
		doGuardedX(() -> {
			PinPullResistance pinRes = (resistor == 0) ? PinPullResistance.OFF : (resistor == 1) ? PinPullResistance.PULL_UP : PinPullResistance.PULL_DOWN;
			if (mode == 1 || mode == 2) {
				gpioPin = controller.provisionDigitalOutputPin(pin, (mode == 1) ? PinState.LOW : PinState.HIGH);
			} else if (mode == 3) {
				gpioPin = controller.provisionDigitalInputPin(pin, pinRes);
				((GpioPinDigitalInput)gpioPin).setDebounce(100);
			}
			this.mode = mode; this.resistor = resistor;
		}, e -> error(log ,e, 10502, medium, "Error setting mode on >{0}< - {1}", getUrl().urlString, e.getMessage()));
	}
	
	public static GpioController getController() { return controller; }
	public GpioPin getGpioPin() { return gpioPin; }
	public int getMode() { return mode; }
	public int getResistor() { return resistor; }

	@Override public void setDigital(boolean high) {
		// TODO validation
		open();
		doGuardedX(() -> {
			GpioPinDigitalOutput p = (GpioPinDigitalOutput) gpioPin;
			if (high) p.high(); else p.low();
			log.info("Set "+(high?"high":"low")+" on "+getUrl());
		}, e -> error(log,e, 10505,medium,"could not write on >{0}< - {1}", getUrl().urlString, e.getMessage()));
	}

	@Override public boolean getDigital() {
		// TODO validation
		open();
		return funcGuardedX(() -> { 
			boolean high = ((GpioPinDigitalInput)gpioPin).getState() == PinState.HIGH;
			log.info("Read "+(high?"high":"low")+" on "+getUrl());
			return high;
		}, e -> error(log,e, 10504,medium,"Could not read on >{0}< - {1}", getUrl().urlString, e.getMessage()));
	}

	@Override public Object listenDigital(Consumer<Boolean> stateListener) {
		// TODO validation
		open();
		GpioPinListenerDigital listener = e -> {
			boolean high = e.getState() == PinState.HIGH;
			stateListener.accept(high);
			log.info("Listend "+(high?"high":"low")+" on "+getUrl());
		};
		doGuardedX(() -> {
System.out.println("ADD LISTENER");
			gpioPin.addListener(listener);
		}, e -> error(log,e, 10503,medium,"could not listen on >{0}< - {1}", getUrl().urlString, e.getMessage()));
		return listener;
	}

	@Override public void stopListen(Object listenerObj) {
		// TODO validation
		doGuarded(() ->	gpioPin.removeListener((GpioPinListener)listenerObj));
	}
	
}
