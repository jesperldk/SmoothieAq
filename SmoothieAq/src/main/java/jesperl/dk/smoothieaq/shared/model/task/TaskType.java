package jesperl.dk.smoothieaq.shared.model.task;

import static jesperl.dk.smoothieaq.shared.model.device.DeviceClass.*;
import static jesperl.dk.smoothieaq.shared.model.device.DeviceType.*;
import static jesperl.dk.smoothieaq.shared.model.measure.MeasurementType.*;

import jesperl.dk.smoothieaq.shared.model.device.*;

public enum TaskType {
	
	auto(10,no,null),
	autoDevice(11,no,auto),
	autoMeasure(20,DeviceClass.sensor,false,false,autoDevice),
	autoOnoff(30,DeviceClass.onoff,true,true,autoDevice),
	autoStatus(31,DeviceClass.status,true,true,autoDevice),
	autoLevel(40,level,true,false,autoDevice,LevelTaskArg.create(7)),
	autoLevelStream(41,level,true,true,autoDevice),
	autoProgram(42,level,true,false,autoDevice,ProgramTaskArg.create(20)),
	autoDoseAmount(50,DeviceClass.doser,false,false,autoDevice,LevelTaskArg.create(7)),
	autoDoseMax(51,DeviceClass.doser,true,true,autoDevice,LevelTaskArg.create(7)),
	
	manual(100,no,null),
	
	other(110,manual,DescriptionTaskArg.create()),

	manualDosing(120,no,manual),
	dosing(121,manualDosing,ValueTaskArg.create(volume,0,null,null)),
	dryDosing(122,manualDosing,ValueTaskArg.create(weight,0,null,null)),

	manualMeassure(130,manual,MeasurementTaskArg.create()),

	maintenanceDevice(140,manual),
	calibrate(141,maintenanceDevice),
	checkAndRefill(142,DeviceClass.container,maintenanceDevice),
	
	clean(150,maintenanceDevice),
	cleanPrefilter(151,filter,clean),
	cleanMainfilter(152,filter,clean),
	cleanTubes(153,filter,clean),

	maintenanceTank(160,manual),
	changeWater(161,tank,maintenanceTank,ValueTaskArg.create(change,0,null,ValueTaskArg.water)),
	topUpWater(162,tank,maintenanceTank,ValueTaskArg.create(volume,0,null,ValueTaskArg.water)),
	cleanPanels(163,tank,maintenanceTank),
	;
	
	private int id;
	private DeviceClass deviceClass;
	private DeviceType deviceType;
	private boolean intervalSchedule;
	private boolean whenAllowed;
	private TaskType parrentType;
	private TaskArg taskArg;
	
	private TaskType(int id, TaskType parrentType) { this(id,DeviceClass.manual,false,false,parrentType,null); }
	private TaskType(int id, TaskType parrentType, TaskArg taskArg) { this(id,DeviceClass.manual,false,false,parrentType,taskArg); }
	private TaskType(int id, DeviceType deviceType, TaskType parrentType) { this(id,DeviceClass.manual,deviceType,false,false,parrentType,null); }
	private TaskType(int id, DeviceType deviceType, TaskType parrentType, TaskArg taskArg) { this(id,DeviceClass.manual,deviceType,false,false,parrentType,taskArg); }
	private TaskType(int id, DeviceClass deviceClass, TaskType parrentType) { this(id,deviceClass,false,false,parrentType,null); }
	private TaskType(int id, DeviceClass deviceClass, boolean intervalSchedule, boolean whenAllowed, TaskType parrentType) { this(id,deviceClass,intervalSchedule,whenAllowed,parrentType,null); }
	private TaskType(int id, DeviceClass deviceClass, boolean intervalSchedule, boolean whenAllowed, TaskType parrentType, TaskArg taskArg) { this(id, deviceClass, defaultType(deviceClass), intervalSchedule,whenAllowed, parrentType, taskArg); }
	private TaskType(int id, DeviceClass deviceClass, DeviceType deviceType, boolean intervalSchedule, boolean whenAllowed, TaskType parrentType, TaskArg taskArg) {
		this.id = id;
		this.deviceClass = deviceClass;
		this.deviceType = deviceType;
		this.intervalSchedule = intervalSchedule;
		this.whenAllowed = whenAllowed;
		this.parrentType = parrentType;
		this.taskArg = taskArg;
	}
	private static DeviceType defaultType(DeviceClass deviceClass) {
		if (deviceClass == DeviceClass.sensor) return DeviceType.sensor;
		if (deviceClass == DeviceClass.doser) return DeviceType.doser;
		if (deviceClass == DeviceClass.status) return DeviceType.status;
		return null;
	}
	
	public DeviceClass getDeviceClass() { return deviceClass; }
	public DeviceType getDeviceType() { return deviceType; }
	public boolean isIntervalSchedule() { return intervalSchedule; }
	public boolean isWhenAllowed() { return whenAllowed; }
	public TaskType getParrentType() { return parrentType; }
	public boolean isOfType(TaskType type) { return this.equals(type) || (parrentType != null && parrentType.equals(type)); }
	public TaskArg getTaskArg() { return taskArg; }
	public int getId() { return id; }
}
