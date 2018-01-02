package jesperl.dk.smoothieaq.client.components.tstable;

import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.ui.*;

import gwt.material.design.client.ui.html.*;

public class TsRow extends Div {
	private int no;
	
	private Div iconDiv = new Div("TsIcon");
	private Div stampDiv = new Div("TsStamp");
	private Div idDiv = new Div("TsId");
	private Div textDiv = new Div("TsText");

	public TsRow(int no) {
		super("TsRow");
		this.no = no;
		add(iconDiv);
		add(stampDiv);
		add(idDiv);
		add(textDiv);
	}
	
	@Override protected void onLoad() { GWT.log("~onLoad "+no);
		super.onLoad();
	}
	
	@Override
	protected void onUnload() { GWT.log("~onUnload "+no);
		super.onUnload();
	}
	
	public void data(TsRowData rd) { GWT.log("~data "+no);
		draw(rd.icon(),rd.stampTxt(),rd.id(),rd.text(),rd.style());
	}

	public void empty() { GWT.log("~empty "+no);
		draw(null,null,null,"&nbsp;",null);
	}
	
	protected void draw(Widget icon, String stamp, String id, String text, String style) {  GWT.log("~draw "+stamp+" "+id+" "+text);
		set(iconDiv,icon);
		set(stampDiv,stamp);
		set(idDiv,id);
		Widget htmlText = set(textDiv,text);
		if (htmlText != null && style != null) htmlText.setStyleName(style);
	}
	
	protected static Widget set(Div div, String str) { return set(div,funcNotNull(str, s -> new HTML(s))); }
	
	protected static Widget set(Div div, Widget widget) {
		div.clear();
		if (widget != null) div.add(widget);
		return widget;
	}
	
}
