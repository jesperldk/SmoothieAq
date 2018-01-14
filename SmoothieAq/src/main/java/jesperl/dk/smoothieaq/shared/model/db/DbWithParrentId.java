package jesperl.dk.smoothieaq.shared.model.db;

import com.google.gwt.core.shared.*;

import jsinterop.annotations.*;

@DbVersion(1) @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public abstract class  DbWithParrentId extends DbWithStamp implements Idable, DbWithParrentId_Helper {

	public int id; 

	@Override @GwtIncompatible
	public boolean equals(Object obj) {
		return obj != null && obj.getClass().equals(this.getClass()) && id != 0 && id == ((Idable)obj).getId();
	}
	
	@Override @GwtIncompatible
	public String toString() {
		return getClass().getSimpleName() + "#" + getId();
	} 
	
	@Override @GwtIncompatible
	public int hashCode() {
		assert id != 0;
		return id;
	}

	@Override @GwtIncompatible
	public void setId(int id) {
		assert this.id == 0;
		this.id = id;
	}

	@Override @GwtIncompatible
	public int getId() {
		assert id != 0;
		return id;
	}

}
