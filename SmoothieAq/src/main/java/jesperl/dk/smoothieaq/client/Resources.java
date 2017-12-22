package jesperl.dk.smoothieaq.client;

import com.google.gwt.core.client.*;
import com.intendia.gwt.autorest.client.*;

import jesperl.dk.smoothieaq.shared.resources.*;

public abstract class  Resources {

	public static DeviceRest device = new DeviceRest_RestServiceModel(()->osm());

	static ResourceVisitor osm() { return new RequestResourceBuilderX().path(GWT.getModuleBaseURL()+"x/"); }
}
