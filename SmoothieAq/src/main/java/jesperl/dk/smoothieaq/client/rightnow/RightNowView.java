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
package jesperl.dk.smoothieaq.client.rightnow;

import org.moxieapps.gwt.highcharts.client.*;

import com.google.gwt.core.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import gwt.material.design.client.ui.*;
import jesperl.dk.smoothieaq.client.*;

public class RightNowView  extends Composite {
    interface Binder extends UiBinder<Widget, RightNowView> {}
	private static Binder binder = GWT.create(Binder.class);
	
	@UiField
	MaterialPanel panel;
	
    public RightNowView() {
        initWidget(binder.createAndBindUi(this));
    }
    
    @Override
    protected void onLoad() {
        chart();
		Resources.device.get(1).subscribe(d -> panel.add(new Label("device: "+d.description)));
    }

    public void chart()
    {
    	Chart chart = new Chart()
    	    .setType(Series.Type.SPLINE)
    	    .setChartTitleText("Nice Chart")
    	    .setMarginRight(10);

    	  // Add data series to the chart
    	  Series series = chart.createSeries()
    	    .setPoints(new Number[] { 163, 203, 276, 408, 547, 729, 628 });
    	  chart.addSeries(series);

    	  // Add the chart to any GWT container as a normal widget
    	  panel.add(chart);
    }
}
