package jesperl.dk.smoothieaq.shared.model.db;

import java.time.*;
import java.util.*;

import com.fasterxml.jackson.annotation.*;
import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.shared.*;
import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public abstract class  DbWithStamp extends DbObject { 
	
	public long stamp; 
	@JsonIgnore transient private Date date;
	@GwtIncompatible @JsonIgnore transient private Instant instant;

//	 TODO
//	@GwtIncompatible // because of isNative
//	public DbWithStamp() {
//		stamp = SimpleState.state().now().getTime();
//	}
	
	@JsOverlay
	public final long getStamp() {
		if (stamp == 0) stamp = SimpleState.state().now().getTime();
		return stamp;
	}
	
	@JsOverlay
	public final Date getDate() {
		if (date == null) date = new Date(getStamp());
		return date;
	}
	
	@GwtIncompatible 
	public Instant getInstant() {
		if (instant == null) instant = getDate().toInstant();
		return instant;
	}
	
	@Override @GwtIncompatible 
	public int hashCode() {
		return getDate().hashCode();
	}

	// TODO
//	@Override @JsOverlay
//	public Object clone() throws CloneNotSupportedException {
//		DbWithStamp clone = (DbWithStamp) super.clone();
//		clone.stamp = SimpleState.state().now().getTime();
//		clone.date = null;
//		return clone;
//	}
}
