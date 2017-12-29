/*
 * #%L
 * GwtMaterial
 * %%
 * Copyright (C) 2015 - 2017 GwtMaterialDesign
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package jesperl.dk.smoothieaq.client.devices;

import static jesperl.dk.smoothieaq.client.context.ClientContext.*;
import static jesperl.dk.smoothieaq.client.css.SmoothieAqCss.*;

import com.google.gwt.core.client.*;
import com.google.gwt.resources.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import static jesperl.dk.smoothieaq.client.components.GuiUtil.*;

import gwt.material.design.client.constants.*;
import gwt.material.design.client.ui.*;
import jesperl.dk.smoothieaq.client.*;
import jesperl.dk.smoothieaq.client.devices.img.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.resources.DeviceRest.*;
import rx.*;

public class DevicesView extends Composite {
    interface Binder extends UiBinder<Widget, DevicesView> {}
	private static Binder binder = GWT.create(Binder.class );
	
	@UiField MaterialRow cardRow;
	
	@UiField MaterialButton addBtn;
	
	private Subscription devicesSubscription;

    public DevicesView() {
        initWidget(binder.createAndBindUi(this));
    }
    
    @Override
    protected void onLoad() {
    	super.onLoad();
        addBtn.addClickHandler(evt -> createDeviceModal());

//        devicesSubscription = ctx.cDevices.devices().flatMap(cd -> cd.compactView).subscribe(d -> cardRow.add(deviceCol(d)));
        devicesSubscription = ctx.cDevices.devices()
        	.map(cd -> cd.compactView.map(this::deviceCard).map(this::deviceCol))
        	.map(WSingle::new).subscribe(cardRow::add);
    }
    
    public static class WSingle extends MaterialContainer {
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
    
    @Override
    protected void onUnload() {
    	devicesSubscription.unsubscribe();
    	super.onUnload();
    }
    
    protected Widget createDeviceModal() {
    	MaterialTitle title = new MaterialTitle("*New device", "***");
    	MaterialPanel panel = new MaterialPanel();
    	Device device = new Device();
    	panel.add(wListBox(device.deviceType()));
    	panel.add(wListBox(device.deviceClass()));
    	panel.add(wListBox(device.deviceCategory()));
//    	panel.add(wShortBox(device.driverId()));
    	panel.add(wListBox(device.driverId(),ctx.cDrivers.options()));
    	panel.add(wTextBox(device.deviceUrl()));
    	panel.add(wTextBox(device.name()));
    	panel.add(wTextBox(device.description()));
    	panel.add(wListBox(device.measurementType()));
    	panel.add(wFloatBox(device.repeatabilityLevel()));
    	panel.add(wFloatBox(device.onLevel()));
    	panel.add(wFloatBox(device.wattAt100pct()));
    	return wModal(title,panel, () -> { GWT.log("call create device"); t(device);});
    }

	Subscription t(Device device) {
		return Resources.device.create(device.copy()).subscribe(e -> GWT.log("create device ok - "+e),e -> GWT.log("create device err - "+e));
	}
    
	protected Widget deviceCol(DeviceCompactView d) {
		MaterialColumn column = new MaterialColumn(12, 6, 4);
		column.add(deviceCard(d));
		return column;
	}

	protected Widget deviceCol(Widget w) {
		MaterialColumn column = new MaterialColumn(12, 6, 4);
		column.add(w);
		return column;
	}

	private Widget deviceCard(DeviceCompactView d) {
		MaterialCard card = new MaterialCard();
		card.setOrientation(Orientation.LANDSCAPE);
		card.addStyleName(css.aqcard());
		
		MaterialCardImage cardImage = new MaterialCardImage();
		cardImage.add(new MaterialImage(get(d.deviceClass)));
		card.add(cardImage);
		
		MaterialCardContent content = new MaterialCardContent();
		MaterialCardTitle title = new MaterialCardTitle();
		title.setText(d.name);
		content.add(title);
		content.add(new MaterialLabel(d.description));
		content.add(new DeviceStatusView(d));
		card.add(content);
		
		
		MaterialCardAction action = new MaterialCardAction();
		action.add(new MaterialLink("Edit"));
		action.add(new MaterialLink("View"));
		card.add(action);
		
		return card;
	}
	
	public static ImageResource get(DeviceType devClass) {
		DeviceImages img = DeviceImages.img;
		if (devClass == DeviceType.filter) return img.filter();
		if (devClass == DeviceType.generic) return img.generic();
		return img.generic();
	}
}
