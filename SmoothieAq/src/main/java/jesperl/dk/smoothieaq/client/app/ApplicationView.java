package jesperl.dk.smoothieaq.client.app;

import static gwt.material.design.jquery.client.api.JQuery.*;
import static jesperl.dk.smoothieaq.client.context.ClientContext.*;

import java.util.function.*;

import com.google.gwt.core.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import gwt.material.design.client.constants.*;
import gwt.material.design.client.ui.*;
import jesperl.dk.smoothieaq.client.*;
import jesperl.dk.smoothieaq.client.css.*;
import jesperl.dk.smoothieaq.client.devices.*;
import jesperl.dk.smoothieaq.client.rightnow.*;
import jesperl.dk.smoothieaq.shared.model.event.*;
import rx.*;

public class  ApplicationView  extends Composite {
    interface Binder extends UiBinder<Widget, ApplicationView> {}
	private static Binder binder = GWT.create(Binder.class );
	
	@UiField AppMessages msg = SmoothieAq.appMessages;
	
	@UiField MaterialSideNavDrawer sidenav;

	@UiField MaterialContainer main;
	
    public ApplicationView() {
        initWidget(binder.createAndBindUi(this));
        SmoothieAqCss.bndl.css().ensureInjected();
        menuItem(msg.menuRightnow(), IconType.TIMELINE, ()->new RightNowView());
        menuItem(msg.menuDevices(), IconType.DEVICE_HUB, ()->new DevicesView());
        menuItem("Settings", IconType.SETTINGS, ()->new DevicesView());
        menuItem("Help", IconType.HELP_OUTLINE, ()->new DevicesView());
        menuItem("About", IconType.COPYRIGHT, ()->new DevicesView());
    }
    
    @Override
    protected void onLoad() {
    	super.onLoad();
    	try {
    		Observable<Event> events = Resources.event.events().share();
			events.filter(Event::instanceOfErrorEvent).map(Event::asErrorEvent).doOnNext(e -> GWT.log("ErrorEvent: "+e.error.defaultMessage)).subscribe(e -> MaterialToast.fireToast(e.error.format()));
			events.filter(Event::instanceOfMessageEvent).map(Event::asMessageEvent).subscribe(e -> MaterialToast.fireToast(e.message.format()));
			events.filter(Event::instanceOfDeviceChangeEvent).map(Event::asDeviceChangeEvent).subscribe(e -> ctx.cDevices.deviceChanged(e.compactView));
    	} catch (Exception e) {}
    }
    
    protected void menuItem(String text, IconType iconType, Supplier<Widget> target) {
        MaterialIcon icon = new MaterialIcon(iconType);
        icon.addStyleName("left"); // Well???
		MaterialLink link = new MaterialLink(text, icon);
        link.addClickHandler(e-> showOnMain(target.get()));
		sidenav.add(link);
    }
    
    protected void showOnMain(Widget child) {
		main.clear();
		$("#sidenav-overlay").click();
		main.add(child);
		main.setFocus(true);
    }

}
