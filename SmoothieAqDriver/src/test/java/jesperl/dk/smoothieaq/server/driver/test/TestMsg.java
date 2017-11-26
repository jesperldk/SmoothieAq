package jesperl.dk.smoothieaq.server.driver.test;

import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.util.shared.error.Severity.*;

public class  TestMsg {

	public static void main(String[] args) {
		System.out.println(msg(27, "message arg1:{0} arg2:{1}", "111","222"));
		System.out.println(error(28, minor, "message arg1:{0} arg2:{1}", "111","222").getError());
		System.out.println(error(29, minor, "message arg1:{0} arg2:{1}", "111","222"));
		System.out.println(error(28, minor, "message arg1:{0} arg2:{1}", "111","222").getMessage());
	}

}
