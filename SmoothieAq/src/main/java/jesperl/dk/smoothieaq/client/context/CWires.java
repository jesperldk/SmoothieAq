package jesperl.dk.smoothieaq.client.context;

import static jesperl.dk.smoothieaq.client.context.CContext.*;

import com.google.gwt.core.client.*;

import gwt.material.design.client.ui.*;
import jesperl.dk.smoothieaq.client.*;
import jesperl.dk.smoothieaq.shared.model.event.*;
import rx.*;

public class CWires {
	
//	public

	public void init() {
		Observable<Event> events = Resources.event.events().share();
		events.filter(Event::instanceOfErrorEvent).map(Event::asErrorEvent).doOnNext(e -> GWT.log("ErrorEvent: "+e.error.defaultMessage)).subscribe(e -> MaterialToast.fireToast(e.error.format()));
		events.filter(Event::instanceOfMessageEvent).map(Event::asMessageEvent).subscribe(e -> MaterialToast.fireToast(e.message.format()));
		events.filter(Event::instanceOfDeviceChangeEvent).map(Event::asDeviceChangeEvent).subscribe(e -> ctx.cDevices.deviceChanged(e.compactView));
	}
}
