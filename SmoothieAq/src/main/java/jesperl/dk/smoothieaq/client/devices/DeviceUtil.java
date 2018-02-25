package jesperl.dk.smoothieaq.client.devices;

import static jesperl.dk.smoothieaq.client.ClientObjects.*;
import static jesperl.dk.smoothieaq.client.text.AppMessages.*;
import static jesperl.dk.smoothieaq.shared.model.device.DeviceClass.*;

import java.util.*;
import java.util.logging.*;

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
	private final static Logger log = Logger.getLogger(DeviceUtil.class .getName());

	public static MaterialWidget actions(CDevice cd, DeviceCompactView dc) {
		try {
			Span span = new Span();
			if (dc.deviceClass != manual)
				span.add(wIconButton(IconType.TIMELINE, null, appMsg.deviceGraph(), () -> {}));
			
			if (dc.deviceClass != manual)
				span.add(wBadge(wIconButton(IconType.ERROR, null, appMsg.deviceErrorNone(), () -> {}), 
						Observable.just(Color.PURPLE), cd.stream(DeviceStream.error).map(m -> m.value), true));
			
			if (EnumSet.of(sensor, toggle, container, calculated).contains(dc.deviceClass))
				span.add(wBadge(wIconButton(IconType.NOTIFICATIONS_NONE, null, appMsg.deviceAlarm(), () -> {}),
						cd.stream(DeviceStream.alarm).map(m -> m.value > 1.9f ? Color.RED : Color.YELLOW), cd.stream(DeviceStream.alarm).map(m -> m.value > 0.9f ? 1f : 0f), true));
			
			if (EnumSet.of(onoff, level, doser, status).contains(dc.deviceClass))
				span.add(wIconButton(IconType.SCHEDULE, null, appMsg.deviceSchedule(), () ->
					cd.autoTask.first().subscribe(ct -> wModal(new TaskEditView(cd, ct.getCurrentCompactView().task, false, true), null))));
			
			span.add(wBadge(wIconButton(IconType.DATE_RANGE, null, appMsg.deviceTasks(), () ->  wModal(new DeviceManualTasksView(cd), null)), 
					Observable.just(Color.BLUE), cd.stream(DeviceStream.duetask).map(m -> m.value), false));
			
			span.add(wIconButton(IconType.EDIT, null, appMsg.edit(), () -> {
				cd.device.subscribe(d -> { 
					Device device = d.copy(); wModal(new DeviceEditView(device,false), () -> 
					Resources.device.update(device.copy()).subscribe(robs())); });
			}));
			return span;
		} catch (Throwable e) { log.log(Level.SEVERE, "øv", e); throw e; }
	}

}
