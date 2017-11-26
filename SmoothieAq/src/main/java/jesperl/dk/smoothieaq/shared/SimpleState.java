package jesperl.dk.smoothieaq.shared;

import java.util.*;

public class SimpleState {
	protected static SimpleState state;
	static { if (state == null) state = new SimpleState(); }
	
	public static SimpleState state() { return state; }
	
	public Date now() { return new Date(); }
}
