package jesperl.dk.smoothieaq.client.tasks;

import static jesperl.dk.smoothieaq.client.ClientObjects.*;
import static jesperl.dk.smoothieaq.client.tasks.TaskUtil.*;

import static jesperl.dk.smoothieaq.client.components.GuiUtil.*;

import gwt.material.design.client.constants.*;
import gwt.material.design.client.ui.*;
import gwt.material.design.client.ui.html.*;
import jesperl.dk.smoothieaq.client.*;
import jesperl.dk.smoothieaq.client.components.*;
import jesperl.dk.smoothieaq.client.context.*;
import jesperl.dk.smoothieaq.shared.model.task.*;
import rx.*;

public class DeviceManualTasksView extends Div {

	private CDevice cDevice;
	private Subscription taskSubscription;

	public DeviceManualTasksView(CDevice cDevice) {
		this.cDevice = cDevice;
 	}
    @Override
    protected void onLoad() {
    	super.onLoad();
       	add(new MaterialTitle("Tasks for device "+cDevice.getCurrentCompactView().name)); // TODO text
    	MaterialPanel panel = new MaterialPanel();
    	MaterialPanel list = new MaterialPanel();
    	panel.add(list);
    	panel.add(wButton(IconType.ADD, true, "Add new task", null, () -> { // TODO text
    		Task task = new Task();
    		task.deviceId = cDevice.id;
    		wModal(new TaskEditView(cDevice, task, true, false), () -> Resources.task.create(task.copy()).subscribe(robs()));
    	}));
    	add(panel);

        taskSubscription = cDevice.manualTasks
        	.map(ct -> ct.compactView.map(pt -> {
        		Div div = new Div();
        		div.add(new MaterialLabel(format(pt.b.task)));
        		div.add(wSingle(ct.scheduleView.map(sv -> new MaterialLabel(schedule(sv)))));
        		div.add(actions(pt.a, pt.b));
        		return div;
        	}))
        	.map(GuiUtil::wSingle)
        	.subscribe(list::add);
    }
    
    @Override
    protected void onUnload() {
    	taskSubscription.unsubscribe();
    	clear();
    	super.onUnload();
    }
    
}
