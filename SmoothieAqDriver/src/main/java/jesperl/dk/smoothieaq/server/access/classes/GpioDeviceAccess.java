package jesperl.dk.smoothieaq.server.access.classes;

import java.util.function.*;

public interface GpioDeviceAccess extends DeviceAccess {
	
	public static final int digitalOutStartLow = 1;
	public static final int digitalOutStartHigh = 2;
	public static final int digitalIn = 3;
	public static final int analogIn = 4;
	public static final int analogOut = 5;
	public static final int pwmOut = 6;
	
	public static final int noneResistor = 0;
	public static final int pullUpResistor = 1;
	public static final int pullDownResistor = 2;

	void setModeDigital(int mode, int resistor);
	default void setModeDigital(int mode) { setModeDigital(mode, 0); }
	void setDigital(boolean high);
	boolean getDigital();
	Object listenDigital(Consumer<Boolean> stateListener);
	void stopListen(Object listenerObj);
}