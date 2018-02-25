package jesperl.dk.smoothieaq.client.components.tstable;

import com.google.gwt.dom.client.Style.*;

import static jesperl.dk.smoothieaq.client.components.GuiUtil.*;

import gwt.material.design.client.ui.html.*;
import jesperl.dk.smoothieaq.client.timeseries.*;

public class TsView extends Div {
	
	private Div tableDiv;

	public TsView(TsSource<TsRowData> source) {
		setWidth("100%"); 
		tableDiv = new Div(); tableDiv.setOverflow(Overflow.HIDDEN);
		TsTable table = new TsTable(source);
		tableDiv.add(table);
		add(tableDiv);
		add(wButton(null, true, "Top", "Move to newest element and listen for more", ()->table.toTop()));
		add(wButton(null, false, "Up10", "Move 10 elements up", ()->table.up(10)));
		add(wButton(null, false, "Up", "Move one element up", ()->table.up()));
		add(wButton(null, false, "Down", "Move one element down", ()->table.down()));
		add(wButton(null, false, "Down10", "Move 10 elements down", ()->table.down(10)));
	}
	
	public TsView setTableHeight(String height) {
		tableDiv.setHeight(height);
		return this;
	}
}
