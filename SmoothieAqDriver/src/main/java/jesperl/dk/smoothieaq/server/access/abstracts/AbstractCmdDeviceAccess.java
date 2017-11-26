package jesperl.dk.smoothieaq.server.access.abstracts;

import static jesperl.dk.smoothieaq.util.server.Utils.*;
import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.util.shared.error.Severity.*;

import java.util.*;
import java.util.logging.*;

import jesperl.dk.smoothieaq.server.access.classes.*;
import jesperl.dk.smoothieaq.util.shared.error.*;


/**
 * Implementation of a subset of http://playground.arduino.cc/Code/CmdMessenger
 */
public abstract class  AbstractCmdDeviceAccess extends WrapperDeviceAccess<ByteDeviceAccess> implements CmdDeviceAccess {
	private final static Logger log = Logger.getLogger(AbstractCmdDeviceAccess.class .getName());

	public int maxSimpleRetries = 3;
	public int maxReopenRetries = 3;
	public int waitOnSimpleRetryMilis = 300; // millis
	public int waitOnReopenRetry = 3000; // millis
	
	protected boolean noretries = false;
	
	public void setNoretries(boolean noretries) { this.noretries = noretries; }
	
	@Override synchronized public Cmd doCmd(Cmd cmd) {
		for (int n=0; n < (noretries ? 1 : maxReopenRetries)-1; n++) {
			try { return doCmdOnce(cmd); } catch (ErrorException ee) {}
			reopen();
			reallySleep(waitOnReopenRetry);
		}
		return doCmdOnce(cmd);
	}
	
	protected Cmd doCmdOnce(Cmd cmd) {
		final Byte endByte = new Byte((byte)endChar.charAt(0));
		return funcGuarded(() -> {
			Cmd responseCmd = da().writeThenRead(stringify(cmd).getBytes(), 0, endByte, noretries ? 1 : maxSimpleRetries, waitOnSimpleRetryMilis, 
				bs -> cmdify(new String(bs)));
			log.fine("cmd: "+stringify(cmd)+" --> "+stringify(responseCmd));
			return responseCmd;
		});
	}
	
//	synchronized Error getLastError() { return lastError; }
	
	protected String stringify(Cmd cmd) {
		return cmd.cmdNo+sepChar+String.join(sepChar, cmd.args)+endChar;
	}
	
	protected Cmd cmdify(String cmdString) {
		return funcGuardedX(() -> {
			String[] split = cmdString.substring(0, cmdString.length()-1).split(String.valueOf(sepChar));
			return new Cmd(Integer.parseInt(split[0]),split.length > 1 ? Arrays.copyOfRange(split, 1, split.length) : new String[0]);
		}, e -> error(log,e,10400,minor,"malformed reply >{0}<",cmdString));
	}

//	protected String doCmd(String cmd) { // will never throw an exception
//		log.info("doing cmd "+cmd+" on "+port.getDescriptivePortName());
//		try {
//			byte[] buf = new byte[bufLength];
//
////			port.setComPortTimeouts(TIMEOUT_NONBLOCKING, 0, 0);
//			while (true) { // flush
//				int read = port.readBytes(buf, bufLength);
//				if (read <= 0) break;
//				log.fine("flushed: "+new String(buf,0,read));
//			}
//			reallySleep(10);
//
////			port.setComPortTimeouts(TIMEOUT_READ_SEMI_BLOCKING | TIMEOUT_WRITE_SEMI_BLOCKING, 500, 500);
//			if (port.writeBytes(cmd.getBytes(), cmd.length()) < cmd.length()) return error(1,medium,"Could not write command '%S' on '%S'",cmd,portDescriptor);
//			log.fine("wrote: "+cmd);
//			reallySleep(50);
//			
//			long start = System.currentTimeMillis(); 
//			StringBuffer reply = new StringBuffer();
//			
//			while (true) {
//				int read = port.readBytes(buf, bufLength);
//				if (read == -1) {
//					return error(6,minor,"Error reading after command '%S' on '%S'",cmd,portDescriptor);
//				} else if (read == 0) {
//					reallySleep(50);
//				} else {
//					log.fine("read: "+new String(buf,0,read));
//					for (int p = 0; p < read; p++)
//						if (buf[p] == endChar.charAt(0)) return reply.append(new String(buf, 0, p)).toString();
//					reply.append(new String(buf, 0, read));
//				}
//				if (System.currentTimeMillis() > start+maxDuration) return error(4,minor,"Timeout for command '%S' on '%S'",cmd,portDescriptor);
//			}
//		} catch (Exception e) {
//			return error(7,major,"Exception for command '%S' on '%S': %S",cmd,portDescriptor,e.toString());
//		}
//	}
//	
//	protected Boolean openConnection() { // will never throw an exception
//		if (port != null) return Boolean.TRUE;
//		try {
//			for (SerialPort somePort: SerialPort.getCommPorts()) {
//				if (somePort.getSystemPortName().equalsIgnoreCase(portDescriptor)) port = somePort;
//				log.fine("found port: "+somePort.getDescriptivePortName()+" - "+(port==null?"not match":"match!!"));
//			}
//			if (port == null) return error(11,major,"Could not find port %S",portDescriptor);
////			port = SerialPort.getCommPort(portDescriptor);
//			log.info("Opening connection on "+port.getDescriptivePortName());
//			if (!port.openPort()) { port = null; return error(8,minor,"Could not open %S",portDescriptor); }
//			port.setComPortParameters(baudRate, 8, ONE_STOP_BIT, NO_PARITY);
//			port.setFlowControl(FLOW_CONTROL_DISABLED);
//
//			reallySleep(waitAfterOpen);
//			return Boolean.TRUE;
//		} catch (Exception e) {
//			closeConnection();
//			return error(9,major,"Exception opening '%S': %S",port,e.toString());
//		}
//	}
	
//	synchronized public void closeConnection() {
//		try {
//			if (port != null) {
//				log.info("Closing connection on "+port.getDescriptivePortName());
//				port.closePort();
//			}
//		} catch (Exception e) {
//			// ignore
//		} finally {
//			port = null;
//		}
//	}
//	
//	@Override protected void finalize() throws Throwable {
//		super.finalize();
//		closeConnection();
//	}

}
