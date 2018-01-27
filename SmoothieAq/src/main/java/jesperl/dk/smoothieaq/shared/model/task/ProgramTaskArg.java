package jesperl.dk.smoothieaq.shared.model.task;

import java.nio.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class  ProgramTaskArg extends TaskArg implements ProgramTaskArg_Helper { 

	public int startDuration[];
	public float startLevel[];
	public int endDuration[];
	public float endLevel[];
	
	@JsOverlay public static ProgramTaskArg create() {
		ProgramTaskArg taskArg = new ProgramTaskArg();
		taskArg.startDuration = new int[] { 1 };
		taskArg.startLevel = new float[] { 200 };
		taskArg.endDuration = new int[] { 1 };
		taskArg.startLevel = new float[] { 0 };
		return taskArg;
	}
	
	@JsOverlay public static ProgramTaskArg create(float level) {
		ProgramTaskArg taskArg = ProgramTaskArg.create();
		taskArg.startLevel[0] = level;
		return taskArg;
	}

	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { ProgramTaskArg_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public ProgramTaskArg deserialize(int ver, ByteBuffer in, DbContext context) { return ProgramTaskArg_Db.deserializeFields(this, in, context); }


	@SuppressWarnings("unchecked")
	@JsOverlay public final Field<Integer> startDuration(int i) {
		Field<Integer> field = (Field<Integer>) $fields().get("ProgramTaskArg.startDuration."+i);
		if (field == null)
			$fields().put("ProgramTaskArg.startDuration."+i, field = new Field<>(()->((ProgramTaskArg)this).startDuration[i], v->((ProgramTaskArg)this).startDuration[i] = v, "ProgramTaskArg.startDuration."+i, Integer.class));
		return field; 
	}

	@SuppressWarnings("unchecked")
	@JsOverlay public final Field<Float> startLevel(int i) {
		Field<Float> field = (Field<Float>) $fields().get("ProgramTaskArg.startLevel."+i);
		if (field == null)
			$fields().put("ProgramTaskArg.startLevel."+i, field = new Field<>(()->((ProgramTaskArg)this).startLevel[i], v->((ProgramTaskArg)this).startLevel[i] = v, "ProgramTaskArg.startLevel."+i, Float.class));
		return field; 
	}

	@SuppressWarnings("unchecked")
	@JsOverlay public final Field<Integer> endDuration(int i) {
		Field<Integer> field = (Field<Integer>) $fields().get("ProgramTaskArg.endDuration."+i);
		if (field == null)
			$fields().put("ProgramTaskArg.endDuration."+i, field = new Field<>(()->((ProgramTaskArg)this).endDuration[i], v->((ProgramTaskArg)this).endDuration[i] = v, "ProgramTaskArg.endDuration."+i, Integer.class));
		return field; 
	}

	@SuppressWarnings("unchecked")
	@JsOverlay public final Field<Float> endLevel(int i) {
		Field<Float> field = (Field<Float>) $fields().get("ProgramTaskArg.endLevel."+i);
		if (field == null)
			$fields().put("ProgramTaskArg.endLevel."+i, field = new Field<>(()->((ProgramTaskArg)this).endLevel[i], v->((ProgramTaskArg)this).endLevel[i] = v, "ProgramTaskArg.endLevel."+i, Float.class));
		return field; 
	}
}
