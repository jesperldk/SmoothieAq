package jesperl.dk.smoothieaq.client;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.ui.*;

import jesperl.dk.smoothieaq.client.app.*;

public class  SmoothieAq implements EntryPoint {

	@Override
	public void onModuleLoad() {
       RootPanel.get().add(new ApplicationView());
	}
}
