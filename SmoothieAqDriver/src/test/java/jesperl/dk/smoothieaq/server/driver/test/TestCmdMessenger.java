package jesperl.dk.smoothieaq.server.driver.test;


import java.io.*;

import com.fazecast.jSerialComm.*;
import com.pi4j.io.gpio.*;

public class TestCmdMessenger {

	public static void main(String[] args) throws IOException, InterruptedException {
		
		for (SerialPort port: SerialPort.getCommPorts())
			System.out.println(port.getDescriptivePortName()+" - "+port.getSystemPortName());
//        return;
		
//        CmdMessenger cm = new CmdMessenger("com5", e -> System.err.println(e));
//        
//        try { for (int i = 0; i < 5; i++) {
//        	System.out.println("do on");
//        	CmdMessenger.Cmd reply = cm.doCmd(new CmdMessenger.Cmd(4,"1"));
//        	System.out.println("on reply "+(reply==null?"err":reply.cmdNo));
//        
//        	Thread.sleep(2000);
//
//        	System.out.println("do off");
//        	reply = cm.doCmd(new CmdMessenger.Cmd(5,"1"));
//        	System.out.println("off reply "+(reply==null?"err":reply.cmdNo));
//        
//        	Thread.sleep(2000);
//
//        }} finally { cm.closeConnection(); }
		
		
		GpioController gpio = GpioFactory.getInstance();
		
		GpioPinDigitalOutput statusLed = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "statusLed", PinState.LOW);
		statusLed.setShutdownOptions(true, PinState.LOW);
		
		statusLed.high();
		Thread.sleep(1000);
		statusLed.low();
		Thread.sleep(1000);
		statusLed.high();
		
		gpio.shutdown();
	}

}
