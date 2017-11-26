package jesperl.dk.smoothieaq.server.access.classes;

import static jesperl.dk.smoothieaq.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.shared.error.Severity.*;

import jesperl.dk.smoothieaq.shared.error.Error;

/**
 * the interface from http://playground.arduino.cc/Code/CmdMessenger
 * but with fixed separator and end chars.
 */
public interface CmdDeviceAccess extends DeviceAccess {
	static String endChar = ";";
	static String sepChar = ",";

	public static final int commErrorCmd = 299; 
	static public class Cmd {
		public int cmdNo;
		public String[] args;
		public Cmd(int cmdNo, String... args) { this.cmdNo = cmdNo; this.args = args; }
		public Cmd(Error error) {
			cmdNo = commErrorCmd;
			args = new String[error.args.length+2];
			args[0] = new Integer(error.msgNo).toString();
			args[1] = error.defaultMessage;
			for (int i = 0; i < args.length; i++) args[i+2] = error.args[i].toString();
		}
		@Override public String toString() { return cmdNo+","+String.join(sepChar, args)+endChar; }
	}

	Cmd doCmd(Cmd cmd);
	
	default Cmd doCmd(int cmdNo, String... args) { return doCmd(new Cmd(cmdNo, args)); }

	default String[] doCmd(int replyCmdNo, int cmdNo, String... args) { 
		Cmd reply = doCmd(new Cmd(cmdNo, args)); 
		if (reply.cmdNo != replyCmdNo) throw error(10402,major,"Was expecting {0} on cmd {1}, but got {2}",replyCmdNo,cmdNo,reply.toString());
		return reply.args;
	}


}