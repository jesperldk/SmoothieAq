package doit;



import static jesperl.dk.smoothieaq.util.server.Utils.*;

import com.fazecast.jSerialComm.*;

import jesperl.dk.smoothieaq.server.access.*;
import jesperl.dk.smoothieaq.server.access.abstracts.*;
import jesperl.dk.smoothieaq.server.driver.*;

public class  Do2 {

	public static void main(String[] args) throws Exception {
		
		for (SerialPort port: SerialPort.getCommPorts()) {
			System.out.println(port.getDescriptivePortName()+" - "+port.getSystemPortName());
		}

		
		DeviceAccessContext context = new DeviceAccessContext(null);
//		context.setSimulate(true);
		
		System.out.println(context.enumerate(SaqbDeviceAccess.bus));
		System.out.println(context.enumerate(SaqDeviceAccess.bus));
		
		SaqOnoffDriver saq1 = new SaqOnoffDriver(); saq1.init(context, "saq://light/1", null);
		saq1.onoff(true);
		reallySleep(3000);
		saq1.onoff(false);
		reallySleep(3000);
		saq1.release();
		
		context.release();
		
//        return;
		
//        CmdMessenger cm = new CmdMessenger("ttyUSB0", e -> System.err.println(e));
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
		
	}

}
