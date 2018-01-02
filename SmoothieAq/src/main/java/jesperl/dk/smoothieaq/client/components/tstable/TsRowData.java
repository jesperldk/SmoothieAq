package jesperl.dk.smoothieaq.client.components.tstable;

import com.google.gwt.user.client.ui.*;

public interface TsRowData {
	long stamp();
	Widget icon();
	String stampTxt();
	String id();
	String text();
	String style();
}
