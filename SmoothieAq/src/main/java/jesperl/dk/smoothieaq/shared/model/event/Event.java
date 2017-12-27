package jesperl.dk.smoothieaq.shared.model.event;

import com.fasterxml.jackson.annotation.*;

import jsinterop.annotations.*;

@JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, include=JsonTypeInfo.As.PROPERTY, property="$type")
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public abstract class Event implements Event_HelperInheritace, Event_Helper { 
	@JsonIgnore public transient String $type; 

}
