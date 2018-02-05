package jesperl.dk.smoothieaq.client.tasks;

import static jesperl.dk.smoothieaq.client.ClientObjects.*;
import static jesperl.dk.smoothieaq.client.text.AppMessages.*;
import static jesperl.dk.smoothieaq.client.text.EnumMessages.*;
import static jesperl.dk.smoothieaq.client.text.TaskMessages.*;
import static jesperl.dk.smoothieaq.shared.model.task.TaskStatusType.*;
import static jesperl.dk.smoothieaq.shared.model.task.TaskType.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import com.google.gwt.user.client.ui.*;

import static jesperl.dk.smoothieaq.client.components.GuiUtil.*;

import gwt.material.design.client.constants.*;
import gwt.material.design.client.ui.*;
import gwt.material.design.client.ui.html.*;
import jesperl.dk.smoothieaq.client.*;
import jesperl.dk.smoothieaq.client.context.*;
import jesperl.dk.smoothieaq.client.inheritancetypes.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.task.*;
import jesperl.dk.smoothieaq.shared.resources.TaskRest.*;
import rx.*;

public class TaskUtil {

	public static String format(Task task) {
		StringBuffer buf = new StringBuffer();
		if (!task.taskType.isOfType(auto) && !task.taskType.isOfType(other)) buf.append(enumMsg.valueLongName(task.taskType));
		if (task.taskArg != null) {
			if (buf.length() > 0) buf.append(", ");
			buf.append(TaskArgInfo.format(task.taskArg));
		}
		if (buf.length() > 0) buf.append("; ");
		buf.append(ScheduleInfo.format(task.schedule));
		if (isNotEmpty(task.whenStream)) buf.append(taskMsg.when(task.whenStream));
		return capitalize(buf.toString());
	}
	
	public static String schedule(TaskScheduleView scheduleView) {
		StringBuilder buf = new StringBuilder();
		if (scheduleView.manualWaitingFrom != 0)
			buf.append(taskMsg.waitingFrom(formatStampMinutes(scheduleView.manualWaitingFrom)));
		else if (scheduleView.manualPostponedTo != 0)
			buf.append(taskMsg.postponedTo(formatStampMinutes(scheduleView.manualPostponedTo)));
		else if (scheduleView.nextStart != 0)
			buf.append(taskMsg.nextStart(formatStampMinutes(scheduleView.nextStart)));
		else if (scheduleView.nextEnd != 0 && scheduleView.on)
			buf.append(taskMsg.nextEnd(formatStampMinutes(scheduleView.nextEnd)));
		else
			buf.append(taskMsg.noNext());
		if (scheduleView.lastStart != 0)
			buf.append(taskMsg.lastStart(formatStampMinutes(scheduleView.lastStart)));
		return capitalize(buf.toString());
	}
	
	public static Widget actions(CTask cTask, TaskCompactView tcv) {
		return wSingle(cTask.cDevice.flatMapObservable(cd -> cd.compactView).map( pd -> {
			CDevice cDevice = pd.a;
			Span actions = new Span();
			if (!pd.a.isNotDeleted()) return actions;
			if (cTask.isNotDeleted()) {
				boolean autoTask = tcv.task.taskType.isOfType(auto);
				if (!autoTask)
					actions.add(wBadge(wIconButton(IconType.PAN_TOOL, null, appMsg.taskDo(), () -> {}),
							Observable.just(Color.BLUE),
							cTask.scheduleView.map(sv -> sv.manualWaitingFrom != 0 ? 1f : 0f),false));
				
				actions.add(wIconButton(IconType.EDIT, null, appMsg.taskEdit(), () -> {
					Task task = tcv.task.copy();
		    		wModal(new TaskEditView(cDevice, task, false, autoTask), () -> Resources.task.update(task.copy()).subscribe(robs()));
		    	}));
				
				MaterialLink delete = wLink(IconType.DELETE, false, appMsg.taskDelete(), null, () -> 
					Resources.task.statusChange(cTask.id, deleted).subscribe(robs()));
				if (tcv.statusType == disabled) {
					MaterialLink enable = wLink(IconType.PLAY_ARROW, false, appMsg.taskEnable(), null, () -> 
						Resources.task.statusChange(cTask.id, enabled).subscribe(robs()));
					actions.add(wDropdown(IconType.PAUSE, null, appMsg.taskPaused(), enable, delete));
				} else {
					MaterialLink pause = wLink(IconType.PAUSE, false, appMsg.taskPause(), null, () -> 
						Resources.task.statusChange(cTask.id, disabled).subscribe(robs()));
					if (pd.b.statusType != DeviceStatusType.enabled)
						actions.add(wDropdown(IconType.PLAY_CIRCLE_OUTLINE, null, appMsg.taskEnabledDeviceNot(), pause, delete));
					else
						actions.add(wDropdown(IconType.PLAY_ARROW, null, appMsg.taskEnabled(), pause, delete));
				}
			} else {
				actions.add(wIconButton(IconType.DELETE, null, appMsg.taskDeleted(), () -> {}));
			}
			return actions;
		}));
	}
}
