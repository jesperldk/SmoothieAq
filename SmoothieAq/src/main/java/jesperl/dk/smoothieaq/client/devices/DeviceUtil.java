package jesperl.dk.smoothieaq.client.devices;

import static jesperl.dk.smoothieaq.client.ClientObjects.*;
import static jesperl.dk.smoothieaq.client.text.AppMessages.*;
import static jesperl.dk.smoothieaq.shared.model.device.DeviceClass.*;

import java.util.*;

import static jesperl.dk.smoothieaq.client.components.GuiUtil.*;

import gwt.material.design.client.base.*;
import gwt.material.design.client.constants.*;
import gwt.material.design.client.ui.html.*;
import jesperl.dk.smoothieaq.client.*;
import jesperl.dk.smoothieaq.client.context.*;
import jesperl.dk.smoothieaq.client.tasks.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.resources.DeviceRest.*;
import rx.Observable;

public class DeviceUtil {

	public static MaterialWidget actions(CDevice cd, DeviceCompactView dc) {
		Span span = new Span();
		if (dc.deviceClass != manual)
			span.add(wIconButton(IconType.TIMELINE, null, appMsg.deviceGraph(), () -> {}));
		
		if (dc.deviceClass != manual)
			span.add(wBadge(wIconButton(IconType.ERROR, null, appMsg.deviceErrorNone(), () -> {}), 
					Observable.just(Color.PURPLE), cd.stream(DeviceStream.error).map(m -> m.value), true));
		
		if (EnumSet.of(sensor, toggle, container, calculated).contains(dc.deviceClass))
			span.add(wBadge(wIconButton(IconType.NOTIFICATIONS_NONE, null, appMsg.deviceAlarm(), () -> {}),
					cd.stream(DeviceStream.error).map(m -> m.value > 1.9f ? Color.RED : Color.YELLOW), cd.stream(DeviceStream.error).map(m -> m.value > 0.9f ? 1f : 0f), true));
		
		if (EnumSet.of(onoff, level, doser, status).contains(dc.deviceClass))
			span.add(wBadge(wIconButton(IconType.SCHEDULE, null, appMsg.deviceSchedule(), () ->
				cd.autoTask.flatMap(ct -> ct.compactView.first().toSingle()).subscribe(pt -> wModal(new TaskEditView(cd, pt.b.task, false, true), null))), 
					Observable.just(Color.BLUE), cd.stream(DeviceStream.duetask).map(m -> m.value), false));
		
		span.add(wIconButton(IconType.DATE_RANGE, null, appMsg.deviceTasks(), () ->  wModal(new DeviceManualTasksView(cd), null)));
		
		span.add(wIconButton(IconType.EDIT, null, appMsg.edit(), () -> {
			cd.device.subscribe(d -> { 
				Device device = d.copy(); wModal(new DeviceEditView(device,false), () -> 
				Resources.device.update(device.copy()).subscribe(robs())); });
		}));
		return span;
	}

}
