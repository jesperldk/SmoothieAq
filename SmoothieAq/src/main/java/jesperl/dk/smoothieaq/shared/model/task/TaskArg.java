package jesperl.dk.smoothieaq.shared.model.task;

import java.nio.*;

import com.fasterxml.jackson.annotation.*;
import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jesperl.dk.smoothieaq.shared.model.db.*;
import jsinterop.annotations.*;

@JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, include=JsonTypeInfo.As.PROPERTY, property="$type")
@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public abstract class  TaskArg extends DbObject implements TaskArg_Helper { 
	@JsonIgnore public transient String $type; 


	@JsOverlay public final TaskArg copy() { return TaskArg_Helper.$copy( this ); }
	@Override @JsOverlay @GwtIncompatible public abstract TaskArg deserialize(int ver, ByteBuffer in, DbContext context);
}
