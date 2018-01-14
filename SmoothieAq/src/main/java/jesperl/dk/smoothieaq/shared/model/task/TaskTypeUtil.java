package jesperl.dk.smoothieaq.shared.model.task;

import static jesperl.dk.smoothieaq.shared.model.device.DeviceClass.*;
import static jesperl.dk.smoothieaq.shared.model.device.DeviceType.*;
import static jesperl.dk.smoothieaq.shared.model.measure.MeasurementType.*;
import static jesperl.dk.smoothieaq.shared.model.task.TaskType.*;

import java.util.*;

import jesperl.dk.smoothieaq.shared.model.db.*;
import jesperl.dk.smoothieaq.shared.model.device.*;

public class TaskTypeUtil {

	public static class TaskTypeInfo {
		public final DeviceClass deviceClass;
		public final DeviceType deviceType;
		public final boolean intervalSchedule;
		public final boolean whenAllowed;
		public final TaskType parrentType;
		public final TaskArg taskArg;
		public TaskTypeInfo(DeviceClass deviceClass, DeviceType deviceType, boolean intervalSchedule, boolean whenAllowed, TaskType parrentType, TaskArg taskArg) {
			this.deviceClass = deviceClass; this.deviceType = deviceType; this.intervalSchedule = intervalSchedule; this.whenAllowed = whenAllowed; this.parrentType = parrentType; this.taskArg = taskArg;
		}
		public boolean isParrentOfType(TaskType type) { return parrentType != null && parrentType.equals(type); }
	}

	static {
		i(auto,no,null);
		i(autoDevice,no,auto);
		i(autoMeasure,DeviceClass.sensor,false,false,autoDevice);
		i(autoOnoff,DeviceClass.onoff,true,true,autoDevice);
		i(autoStatus,DeviceClass.status,true,true,autoDevice);
		i(autoLevel,level,true,false,autoDevice,LevelTaskArg.create(7));
		i(autoLevelStream,level,true,true,autoDevice);
		i(autoProgram,level,true,false,autoDevice,ProgramTaskArg.create(20));
		i(autoDoseAmount,DeviceClass.doser,false,false,autoDevice,LevelTaskArg.create(7));
		i(autoDoseMax,DeviceClass.doser,true,true,autoDevice,LevelTaskArg.create(7));
	
		i(TaskType.manual,no,null);
	
		i(TaskType.other,TaskType.manual,DescriptionTaskArg.create());

		i(manualDosing,no,TaskType.manual);
		i(dosing,manualDosing,ValueTaskArg.create(volume,0,null,null));
		i(dryDosing,manualDosing,ValueTaskArg.create(weight,0,null,null));

		i(manualMeassure,TaskType.manual,MeasurementTaskArg.create());

		i(maintenanceDevice,TaskType.manual);
		i(calibrate,maintenanceDevice);
		i(checkAndRefill,DeviceClass.container,maintenanceDevice);
	
		i(clean,maintenanceDevice);
		i(cleanPrefilter,filter,clean);
		i(cleanMainfilter,filter,clean);
		i(cleanTubes,filter,clean);

		i(maintenanceTank,TaskType.manual);
		i(changeWater,tank,maintenanceTank,ValueTaskArg.create(change,0,null,ValueTaskArg.water));
		i(topUpWater,tank,maintenanceTank,ValueTaskArg.create(volume,0,null,ValueTaskArg.water));
		i(cleanPanels,tank,maintenanceTank);
	}
	
	public static Map<TaskType, TaskTypeInfo> toInfo = new HashMap<>();
	private static void i(TaskType taskType, TaskType parrentType) { i(taskType,DeviceClass.manual,false,false,parrentType,null); }
	private static void i(TaskType taskType, TaskType parrentType, TaskArg taskArg) { i(taskType,DeviceClass.manual,false,false,parrentType,taskArg); }
	private static void i(TaskType taskType, DeviceType deviceType, TaskType parrentType) { i(taskType,DeviceClass.manual,deviceType,false,false,parrentType,null); }
	private static void i(TaskType taskType, DeviceType deviceType, TaskType parrentType, TaskArg taskArg) { i(taskType,DeviceClass.manual,deviceType,false,false,parrentType,taskArg); }
	private static void i(TaskType taskType, DeviceClass deviceClass, TaskType parrentType) { i(taskType,deviceClass,false,false,parrentType,null); }
	private static void i(TaskType taskType, DeviceClass deviceClass, boolean intervalSchedule, boolean whenAllowed, TaskType parrentType) { i(taskType,deviceClass,intervalSchedule,whenAllowed,parrentType,null); }
	private static void i(TaskType taskType, DeviceClass deviceClass, boolean intervalSchedule, boolean whenAllowed, TaskType parrentType, TaskArg taskArg) { i(taskType, deviceClass, defaultType(deviceClass), intervalSchedule,whenAllowed, parrentType, taskArg); }
	private static void i(TaskType taskType, DeviceClass deviceClass, DeviceType deviceType, boolean intervalSchedule, boolean whenAllowed, TaskType parrentType, TaskArg taskArg) {
		toInfo.put(taskType, new TaskTypeInfo(deviceClass, deviceType, intervalSchedule, whenAllowed, parrentType, taskArg));
	}
	public static TaskTypeInfo info(TaskType taskType) { return toInfo.get(fixup(taskType)); }
	
	private static DeviceType defaultType(DeviceClass deviceClass) {
		if (deviceClass == DeviceClass.sensor) return DeviceType.sensor;
		if (deviceClass == DeviceClass.doser) return DeviceType.doser;
		if (deviceClass == DeviceClass.status) return DeviceType.status;
		return null;
	}
	
	public static TaskType fixup(TaskType taskType) { return EnumField.fixup(TaskType.class, taskType); }
	public static short id(TaskType taskType) { return (short) EnumField.fixup(TaskType.class, taskType).getId(); }

	public static Map<Short, TaskType> fromId = new HashMap<>();
	{ for (TaskType taskType: TaskType.values()) { fromId.put((short) taskType.getId(), taskType); } }
	public static TaskType get(short id) { return fromId.get(id); }
}
