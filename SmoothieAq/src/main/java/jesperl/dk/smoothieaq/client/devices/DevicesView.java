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

public class DevicesView extends Composite {
    interface Binder extends UiBinder<Widget, DevicesView> {}
	private static Binder binder = GWT.create(Binder.class );
	
	@UiField MaterialRow cardRow;
	
	@UiField MaterialButton addBtn;

    public DevicesView() {
        initWidget(binder.createAndBindUi(this));
    }
    
    @Override
    protected void onLoad() {
    	super.onLoad();
        addBtn.addClickHandler(evt -> createDeviceModal());

        Resources.device.devices().doOnError(e -> GWT.log("error getAll - "+e)).forEach(d -> cardRow.add(deviceCol(d)));
    }
    
    protected Widget createDeviceModal() {
    	MaterialTitle title = new MaterialTitle("*New device", "***");
    	MaterialPanel panel = new MaterialPanel();
    	Device device = new Device();
    	panel.add(wTextBox(device.name()));
    	panel.add(wComboBox(device.deviceType()));
    	return wModal(title,panel, () -> { GWT.log("call create device"); Resources.device.create(device).subscribe(e -> GWT.log("create device ok - "+e),e -> GWT.log("create device err - "+e));});
    }
    
	protected Widget deviceCol(DeviceCompactView d) {
		MaterialColumn column = new MaterialColumn(12, 6, 4);
		column.add(deviceCard(d));
		return column;
	}

	private Widget deviceCard(DeviceCompactView d) {
		MaterialCard card = new MaterialCard();
		card.setAxis(Axis.HORIZONTAL);
		card.addStyleName(css.aqcard());
		
		MaterialCardImage cardImage = new MaterialCardImage();
		cardImage.add(new MaterialImage(get(d.deviceClass)));
		card.add(cardImage);
		
		MaterialCardContent content = new MaterialCardContent();
		MaterialCardTitle title = new MaterialCardTitle();
		title.setText(d.name);
		content.add(title);
		content.add(new MaterialLabel(d.description));
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
