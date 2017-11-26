package jesperl.dk.smoothieaq.shared.model.db;

import java.nio.*;

import com.google.gwt.core.shared.*;

import jesperl.dk.smoothieaq.server.db.*;
import jsinterop.annotations.*;

@DbVersion(1)
public class  DbClass extends DbWithId {
	public String className;

	@Override @JsOverlay public DbClass copy() { return DbClass_Db.copy(new DbClass(),this); }  
	@Override @JsOverlay @GwtIncompatible public void serialize(ByteBuffer out, DbContext context) { DbClass_Db.serialize(this, out, context); }
	@Override @JsOverlay @GwtIncompatible public DbClass deserialize(int ver, ByteBuffer in, DbContext context) { return DbClass_Db.deserializeFields(this, in, context); }
}
