package jesperl.dk.smoothieaq.client.devices;

import static jesperl.dk.smoothieaq.client.ClientObjects.*;

import static jesperl.dk.smoothieaq.client.components.GuiUtil.*;

import gwt.material.design.client.constants.*;
import gwt.material.design.client.ui.*;
import gwt.material.design.client.ui.html.*;
import jesperl.dk.smoothieaq.client.*;
import jesperl.dk.smoothieaq.client.components.*;
import jesperl.dk.smoothieaq.client.context.*;
import jesperl.dk.smoothieaq.client.tasks.*;
import jesperl.dk.smoothieaq.shared.model.task.*;
import jesperl.dk.smoothieaq.shared.resources.DeviceRest.*;
import rx.*;

public class DeviceManualTasksView extends Div {

	private CDevice cDevice;
	private Subscription taskSubscription;

	public DeviceManualTasksView(CDevice cDevice, DeviceView deviceView) {
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
        	.map(ct -> ct.compactView.map(cv -> new MaterialLabel(TaskUtil.format(cv.b.task))))
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
