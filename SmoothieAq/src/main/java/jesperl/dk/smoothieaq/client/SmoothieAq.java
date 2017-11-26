package jesperl.dk.smoothieaq.client;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.ui.*;

import jesperl.dk.smoothieaq.client.app.*;

public class  SmoothieAq implements EntryPoint {

	public static AppMessages appMessages = GWT.create(AppMessages.class );

	@Override
	public void onModuleLoad() {
       RootPanel.get().add(new ApplicationView());
//		Resources.device.get(27).subscribe(d -> RootPanel.get().add(new Label("device: "+d.description)));
	}
}
