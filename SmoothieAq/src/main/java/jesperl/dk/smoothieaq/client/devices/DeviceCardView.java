package jesperl.dk.smoothieaq.client.devices;

import static jesperl.dk.smoothieaq.client.css.SmoothieAqCss.*;
import static jesperl.dk.smoothieaq.client.text.AppMessages.*;
import static jesperl.dk.smoothieaq.shared.model.device.DeviceStatusType.*;

import java.util.function.*;

import static jesperl.dk.smoothieaq.client.components.GuiUtil.*;

import gwt.material.design.client.constants.*;
import gwt.material.design.client.ui.*;
import jesperl.dk.smoothieaq.client.*;
import jesperl.dk.smoothieaq.client.context.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.resources.DeviceRest.*;
import jesperl.dk.smoothieaq.util.shared.*;
import rx.*;

public class DeviceCardView extends MaterialCard {

	private CDevice cd;
	private DeviceCompactView dc;
	private MaterialLabel currently;
	Subscription subscription;
	
	public DeviceCardView(Pair<CDevice, DeviceCompactView> p) {
		this.cd = p.a;
		this.dc = p.b;
		setOrientation(Orientation.LANDSCAPE);
		addStyleName(css.aqcard());
		
		MaterialCardImage cardImage = new MaterialCardImage();
		cardImage.add(new MaterialImage(DevicesView.get(dc.deviceType)));
		add(cardImage);
		
		MaterialCardContent content = new MaterialCardContent();
		add(content);

		MaterialCardTitle title = new MaterialCardTitle();
		title.setText(dc.name);
		content.add(title);
		content.add(new MaterialLabel(dc.description));
		
		content.add(new DeviceStatusView(dc));
		if (dc.statusType == enabled || dc.statusType == paused) {
			currently = new MaterialLabel("-");
			content.add(currently);
		}
		
		MaterialCardAction action = new MaterialCardAction();
		action.add(wLink(false, appMsg.edit(), null, () -> {
			cd.device.subscribe(d -> { 
				Device device = d.copy(); wModal(new DeviceEditView(device,false), () -> 
				Resources.device.update(device.copy()).subscribe()); });
		}));
		action.add(wLink(false, appMsg.tasks(), null, () -> cd.deviceView.subscribe(dv -> wModal(new DeviceTasksView(dv), null))));
		action.add(wLink(false, appMsg.view(), null, null));
		add(action);
	}
	
	@Override protected void onLoad() {
		super.onLoad();
		if (currently != null) {
			Function<Float, String> formatter = cd.formatter();
			subscription = cd.stream().map(tm -> formatter.apply(tm.value)).subscribe(currently::setText);
		}
	}
	
	@Override protected void onUnload() {
		if (subscription != null) { subscription.unsubscribe(); subscription = null; }
		super.onUnload();
	}
}
