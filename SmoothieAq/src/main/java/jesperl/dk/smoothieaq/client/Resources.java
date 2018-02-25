package jesperl.dk.smoothieaq.client;

import com.google.gwt.core.client.*;
import com.intendia.gwt.autorest.client.*;

import jesperl.dk.smoothieaq.shared.resources.*;

public abstract class  Resources {

	public static DeviceRest device = new DeviceRest_RestServiceModel(()->osm());
	public static EventRest event = new EventRest_RestServiceModel(()->osm());
	public static TaskRest task = new TaskRest_RestServiceModel(()->osm());
	public static MeasureRest measure = new MeasureRest_RestServiceModel(()->osm());

	static ResourceVisitor osm() { return new SseRequestResourceBuilder().path(GWT.getModuleBaseURL()+"x/"); }
}
