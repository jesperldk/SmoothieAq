package jesperl.dk.smoothieaq.client.components.tstable;

import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import com.google.gwt.user.client.ui.*;

import gwt.material.design.client.ui.html.*;

public class TsRow extends Div {
	
	private Div iconDiv = new Div("TsIcon");
	private Div stampDiv = new Div("TsStamp");
	private Div idDiv = new Div("TsId");
	private Div textDiv = new Div("TsText");

	public TsRow(int no) {
		super("TsRow");
		add(iconDiv);
		add(stampDiv);
		add(idDiv);
		add(textDiv);
	}
	
	@Override protected void onLoad() {
		super.onLoad();
	}
	
	@Override
	protected void onUnload() {
		super.onUnload();
	}
	
	public void data(TsRowData rd) {
		draw(rd.icon(),rd.stampTxt(),rd.id(),rd.text(),rd.style());
	}

	public void empty() {
		draw(null,null,null,"&nbsp;",null);
	}
	
	protected void draw(Widget icon, String stamp, String id, String text, String style) {
		set(iconDiv,icon);
		set(stampDiv,stamp);
		set(idDiv,id);
		Widget htmlText = set(textDiv,text);
		if (htmlText != null && style != null) htmlText.setStyleName(style);
	}
	
	protected static Widget set(Div div, String str) { return set(div,(Widget)funcNotNull(str, s -> new HTML(s))); }
	
	protected static Widget set(Div div, Widget widget) {
		div.clear();
		if (widget != null) div.add(widget);
		return widget;
	}
	
}
