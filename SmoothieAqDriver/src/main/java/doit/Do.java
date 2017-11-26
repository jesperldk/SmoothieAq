package doit;



import static jesperl.dk.smoothieaq.server.util.Utils.*;

import com.fazecast.jSerialComm.*;

import jesperl.dk.smoothieaq.server.access.abstracts.*;
import jesperl.dk.smoothieaq.server.driver.*;
import jesperl.dk.smoothieaq.server.driver.abstracts.*;

public class Do {

	public static void main(String[] args) throws Exception {
		
		for (SerialPort port: SerialPort.getCommPorts())
			System.out.println(port.getDescriptivePortName()+" - "+port.getSystemPortName());
//System.out.println("gpio!!!!!");		
//		GpioController gpio = GpioFactory.getInstance();
//		
//		GpioPinDigitalOutput statusLed = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "statusLed", PinState.LOW);
//		statusLed.setShutdownOptions(true, PinState.LOW);
//		
//		GpioPinDigitalInput statusButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, "statusButton", PinPullResistance.PULL_DOWN);
//		statusButton.setDebounce(100);
//		statusButton.setShutdownOptions(true);
//		statusButton.addListener(new GpioPinListenerDigital() {
//			@Override public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
////				statusLed.setState(event.getState() != PinState.HIGH);
//				System.out.println("XXXXX ");
//			}
//		});
//		statusLed.high();
//		reallySleep(5000);
//		statusLed.low();
//		gpio.unprovisionPin(statusLed);
//		statusLed.removeAllListeners(); gpio.unprovisionPin(statusButton);
//		gpio.shutdown();
		System.out.println("********");
//		reallySleep(2000);

		DeviceAccessContext context = new DeviceAccessContext(null);
//		context.setSimulate(true);

		AbstractToggleDriver<?, ?> button = new GpioToggleDriver(); button.init(context, "pi://gpio/2:3.2", null);
		button.listenOn(on -> System.out.println("XXXXX "+(on?"on":"off")));

		AbstractOnoffDriver<?, ?> onoff = new GpioOnoffDriver(); onoff.init(context, "pi://gpio/1", null);
		onoff.onoff(true);
		reallySleep(2000);
		onoff.onoff(false);
		onoff.release();
		
		
		AbstractSensorDriver<?,?> am2321Humid = new AM2321HumidDriver(); am2321Humid.init(context, am2321Humid.getDefaultUrls(context).get(0), null);
		AbstractSensorDriver<?,?> am2321Temp = new AM2321TempDriver(); am2321Temp.init(context, am2321Temp.getDefaultUrls(context).get(0), null);
//		driver.initialize();
//		driver.read();
		
		for (int i = 0; i < 3; i++) {
			System.out.println("humid: "+am2321Humid.measure()+" temp: "+am2321Temp.measure());
			Thread.sleep(1000);
		}
		
		am2321Humid.release();
		am2321Temp.release();
			
		reallySleep(10000);
//		CN0326Driver cn0326 = new CN0326Driver(0,1000);
//		
//		cn0326.init();
//		cn0326.dumpRegisters();
//		cn0326.dumpMeasure();
//		
//		PhDriver phDriver = new PhDriver(cn0326);
//		phDriver.measurePh();
//
//		phDriver.calibrate1(list(3.98f,4.00f,4.01f,4.03f,4.05f));
//		phDriver.calibrate2(list(6.98f,7.00f,7.01f,7.03f,7.05f));
//		phDriver.calibrateFinalize();
//		phDriver.measurePh();
//		
//		am2321.close();
//		cn0326.close();
		
		context.release();
	}

}
