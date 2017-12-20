package jesperl.dk.smoothieaq.client.components.modal;

import com.google.gwt.core.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import gwt.material.design.client.ui.*;
import jesperl.dk.smoothieaq.util.shared.error.Errors.*;

public class ModalView extends Composite {
    interface Binder extends UiBinder<Widget, ModalView> {}
	private static Binder binder = GWT.create(Binder.class );
	
	@UiField MaterialModal modal;
	@UiField MaterialModalContent content;
	
	@UiField MaterialButton cancel;
	@UiField MaterialButton ok;

    public ModalView(Widget title, Widget body, Action okAction) {
        initWidget(binder.createAndBindUi(this));
        
        Panel parrent = RootPanel.get();
        if (title != null) content.add(title);
        if (body != null) content.add(body);

        Action close = () -> { modal.close(); parrent.remove(this); };
        cancel.addClickHandler(evt -> close.doit());
        if (okAction != null) ok.addClickHandler(evt -> okAction.noException().and(close).doit());
        
        parrent.add(this);
        modal.setFullscreen(true);
        modal.open();
    }
    
}
