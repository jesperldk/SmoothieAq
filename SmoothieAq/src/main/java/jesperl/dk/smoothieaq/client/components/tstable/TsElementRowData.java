package jesperl.dk.smoothieaq.client.components.tstable;

import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import com.google.gwt.user.client.ui.*;

import static jesperl.dk.smoothieaq.client.components.GuiUtil.*;

import jesperl.dk.smoothieaq.client.timeseries.*;

public class TsElementRowData implements TsRowData {
	
	private TsElement element;
	private String stampText;
	private String id;
	
	public TsElementRowData(TsElement element) {
		this.element = element; 
		stampText = formatStamp(element.stamp);
		id = strv(element.deviceId);
	}

	@Override public long stamp() { return element.stamp; }
	@Override public Widget icon() { return new HTML("*"); }
	@Override public String stampTxt() { return stampText; }
	@Override public String id() { return id; }
	@Override public String text() { return ((Integer)element.data).toString(); }
	@Override public String style() { return null; }
}
