package jesperl.dk.smoothieaq.client.text;

import com.google.gwt.core.client.*;
import com.google.gwt.i18n.client.*;

public interface AppMessages extends Messages {
	public static AppMessages appMsg = GWT.create(AppMessages.class );
	
	@DefaultMessage("Right now")
	String menuRightnow();

	@DefaultMessage("Devices")
	String menuDevices();
	
	
	@DefaultMessage("Edit")
	String edit();
	
	@DefaultMessage("View")
	String view();
	
	@DefaultMessage("Ok")
	String ok();
	
	@DefaultMessage("Tasks")
	String tasks();
	
	@DefaultMessage("Cancel")
	String cancel();
	
	@DefaultMessage("Close")
	String close();
	

	@DefaultMessage("New device")
	String newDeviceTitle();

	@DefaultMessage("Edit device")
	String editDeviceTitle();
}
