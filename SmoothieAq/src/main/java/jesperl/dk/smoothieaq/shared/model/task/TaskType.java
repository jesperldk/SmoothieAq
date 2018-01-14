package jesperl.dk.smoothieaq.shared.model.task;

import jesperl.dk.smoothieaq.shared.model.task.TaskTypeUtil.*;

public enum TaskType {
	
	auto(10),
	autoDevice(11),
	autoMeasure(20),
	autoOnoff(30),
	autoStatus(31),
	autoLevel(40),
	autoLevelStream(41),
	autoProgram(42),
	autoDoseAmount(50),
	autoDoseMax(51),
	
	manual(100),
	
	other(110),

	manualDosing(120),
	dosing(121),
	dryDosing(122),

	manualMeassure(130),

	maintenanceDevice(140),
	calibrate(141),
	checkAndRefill(142),
	
	clean(150),
	cleanPrefilter(151),
	cleanMainfilter(152),
	cleanTubes(153),

	maintenanceTank(160),
	changeWater(161),
	topUpWater(162),
	cleanPanels(163),
	;
	
	private int id;

	private TaskType(int id) { this.id = id; }
	
	public int getId() { return id; }
	public TaskTypeInfo info() { return TaskTypeUtil.toInfo.get(this); }
	public boolean isOfType(TaskType type) { return this.equals(type) || info().isParrentOfType(type); }
}
