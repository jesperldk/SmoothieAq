package jesperl.dk.smoothieaq.client.devices.img;

import com.google.gwt.core.client.*;
import com.google.gwt.resources.client.*;


public interface DeviceImages extends ClientBundle {
	public static DeviceImages img = GWT.create(DeviceImages.class );
	
	@Source("filter.jpg") ImageResource filter();
	@Source("generic.jpg") ImageResource generic();
	
}
