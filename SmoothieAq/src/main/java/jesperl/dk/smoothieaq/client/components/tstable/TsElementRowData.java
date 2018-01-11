package jesperl.dk.smoothieaq.client.components.tstable;

import static jesperl.dk.smoothieaq.client.context.CContext.*;

import com.google.gwt.user.client.ui.*;

import static jesperl.dk.smoothieaq.client.components.GuiUtil.*;

import jesperl.dk.smoothieaq.client.timeseries.*;

public abstract class TsElementRowData implements TsRowData {
	
	protected TsElement element;
	protected String stampText;
	protected String id;
	
	public TsElementRowData(TsElement element) {
		this.element = element; 
		stampText = formatStamp(element.stamp);
		id = ctx.cDevices.name(element.deviceId, element.streamId);
	}

	@Override public long stamp() { return element.stamp; }
	@Override public Widget icon() { return new HTML("*"); }
	@Override public String stampTxt() { return stampText; }
	@Override public String id() { return id; }
	@Override public String style() { return null; }
}
