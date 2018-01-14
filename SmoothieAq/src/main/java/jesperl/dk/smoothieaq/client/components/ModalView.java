package jesperl.dk.smoothieaq.client.components;

import static jesperl.dk.smoothieaq.client.text.AppMessages.*;

import com.google.gwt.user.client.ui.*;

import static jesperl.dk.smoothieaq.client.components.GuiUtil.*;

import gwt.material.design.client.constants.*;
import gwt.material.design.client.ui.*;
import jesperl.dk.smoothieaq.util.shared.error.Errors.*;

public class ModalView extends MaterialModal {

    public ModalView(Widget body, Action okAction) {
    	setType(ModalType.DEFAULT);
    	setInDuration(500); setOutDuration(500);
    	
    	MaterialModalContent content = new MaterialModalContent();
    	add(content);
        content.add(body);

        MaterialModalFooter footer = new MaterialModalFooter();
        add(footer);
        
        Panel parrent = RootPanel.get();
        Action close = () -> { close(); parrent.remove(this); };
        if (okAction != null) {
	        footer.add(wButton(null, false, appMsg.cancel(), null, close));
	        footer.add(wButton(null, true, appMsg.ok(), null, okAction.noException().and(close)));
        } else {
        	footer.add(wButton(null, true, appMsg.close(), null, close));
        }
        
        parrent.add(this);
//        setFullscreen(true);
        open();
    }
}
