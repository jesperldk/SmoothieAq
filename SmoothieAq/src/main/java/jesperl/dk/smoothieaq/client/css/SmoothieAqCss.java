package jesperl.dk.smoothieaq.client.css;

import com.google.gwt.core.client.*;
import com.google.gwt.resources.client.*;

public interface SmoothieAqCss extends ClientBundle {
	public static SmoothieAqCss bndl = GWT.create(SmoothieAqCss.class );
	public static Css css = bndl.css();
	
	@Source("SmoothieAq.css")
	Css css();

	public interface Css extends CssResource {
		String aqcard();
	}
}
