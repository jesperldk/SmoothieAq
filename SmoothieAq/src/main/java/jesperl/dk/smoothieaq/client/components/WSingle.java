package jesperl.dk.smoothieaq.client.components;

import com.google.gwt.user.client.ui.*;

import gwt.material.design.client.ui.html.*;
import rx.*;

public class WSingle extends Div {
	private Observable<Widget> observable;
	private Subscription subscription;
	
	public WSingle(Observable<Widget> observable) { this.observable = observable; }
	
	@Override protected void onLoad() {
		super.onLoad();
		subscription = observable.subscribe(w -> { clear(); add(w); });
	}
	@Override protected void onUnload() {
		if (subscription != null) subscription.unsubscribe();
		super.onUnload();
	}
}