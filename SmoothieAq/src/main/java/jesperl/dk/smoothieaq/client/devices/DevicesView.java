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

import static jesperl.dk.smoothieaq.client.context.CContext.*;

import java.util.logging.*;

import com.google.gwt.core.client.*;
import com.google.gwt.resources.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import static jesperl.dk.smoothieaq.client.components.GuiUtil.*;

import gwt.material.design.client.ui.*;
import jesperl.dk.smoothieaq.client.*;
import jesperl.dk.smoothieaq.client.components.*;
import jesperl.dk.smoothieaq.client.devices.img.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import rx.*;

public class DevicesView extends Composite {
	static final Logger log = Logger.getLogger(DevicesView.class.getName());
	
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
        addBtn.addClickHandler(evt -> { Device device = new Device(); wModal(new DeviceEditView(device,true), () -> Resources.device.create(device.copy()).subscribe()); });

        devicesSubscription = ctx.cDevices.devices()
        	.map(cd -> cd.compactView.map(DeviceCardView::new).map(this::deviceCol))
        	.map(GuiUtil::wSingle).subscribe(cardRow::add);
    }
    
    @Override
    protected void onUnload() {
    	devicesSubscription.unsubscribe();
    	super.onUnload();
    }
    
	protected Widget deviceCol(Widget w) {
		MaterialColumn column = new MaterialColumn(12, 6, 4);
		column.add(w);
		return column;
	}
	
	public static ImageResource get(DeviceType devType) {
		DeviceImages img = DeviceImages.img;
		if (devType == DeviceType.filter) return img.filter();
		if (devType == DeviceType.generic) return img.generic();
		return img.generic();
	}
}
