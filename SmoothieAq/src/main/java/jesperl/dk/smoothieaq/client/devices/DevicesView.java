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
import com.google.gwt.user.client.ui.*;

import gwt.material.design.client.constants.*;
import gwt.material.design.client.ui.*;
import jesperl.dk.smoothieaq.client.*;
import jesperl.dk.smoothieaq.client.devices.img.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.resources.DeviceRest.*;

public class DevicesView extends MaterialRow {

	@Override
	protected void onLoad() {
		super.onLoad();
		
		MaterialRow row = new MaterialRow();
		add(row);
		
		Resources.device.devices().doOnError(e -> GWT.log("error getAll - "+e)).forEach(d -> row.add(deviceCol(d)));
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
