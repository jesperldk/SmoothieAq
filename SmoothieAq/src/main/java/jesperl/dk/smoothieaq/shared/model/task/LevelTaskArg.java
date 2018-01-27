package jesperl.dk.smoothieaq.shared.model.task;

import java.nio.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  LevelTaskArg extends TaskArg implements LevelTaskArg_Helper {

	public float level;
	
	@JsOverlay public static LevelTaskArg create() {
		LevelTaskArg taskArg = new LevelTaskArg();
		return taskArg;
	}
	
	@JsOverlay public static LevelTaskArg create(float level) {
		LevelTaskArg taskArg = LevelTaskArg.create();
		taskArg.level = level;
		return taskArg;
	}

	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { LevelTaskArg_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public LevelTaskArg deserialize(int ver, ByteBuffer in, DbContext context) { return LevelTaskArg_Db.deserializeFields(this, in, context); }
}
