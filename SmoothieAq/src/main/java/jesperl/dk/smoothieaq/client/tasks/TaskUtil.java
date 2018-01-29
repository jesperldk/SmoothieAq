package jesperl.dk.smoothieaq.client.tasks;

import static jesperl.dk.smoothieaq.client.text.EnumMessages.*;
import static jesperl.dk.smoothieaq.client.text.TaskMessages.*;
import static jesperl.dk.smoothieaq.shared.model.task.TaskType.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import jesperl.dk.smoothieaq.client.inheritancetypes.*;
import jesperl.dk.smoothieaq.shared.model.task.*;

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
}
