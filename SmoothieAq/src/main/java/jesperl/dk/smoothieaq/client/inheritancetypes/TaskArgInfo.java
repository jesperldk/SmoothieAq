package jesperl.dk.smoothieaq.client.inheritancetypes;

import static jesperl.dk.smoothieaq.client.text.EnumMessages.*;
import static jesperl.dk.smoothieaq.client.text.TaskMessages.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.util.*;
import java.util.function.*;

import static jesperl.dk.smoothieaq.client.components.GuiUtil.*;

import gwt.material.design.client.base.*;
import jesperl.dk.smoothieaq.shared.model.task.*;

public class TaskArgInfo {
	public static final Map<String, InheritanceTypeInfo<TaskArg>> infos = new HashMap<>();
	
	@SuppressWarnings("unchecked") private static <T extends TaskArg> void i(String type, Supplier<T> createNew, BiConsumer<T, MaterialWidget> addFields, Function<T, String> format) {
		infos.put(type, new InheritanceTypeInfo<TaskArg>((Supplier<TaskArg>)createNew, (BiConsumer<TaskArg, MaterialWidget>)addFields, (Function<TaskArg,String>)format));
	}
	static {
		i(".DescriptionTaskArg",
			() -> DescriptionTaskArg.create(),
			(arg, widget) -> {
				widget.add(wTextBox(arg.description()));
			},
			arg -> taskMsg.descriptionTaskArg(arg.description)
		);
		i(".LevelTaskArg",
			() -> LevelTaskArg.create(),
			(arg, widget) -> {
				widget.add(wFloatBox(arg.level()));
			},
			arg -> taskMsg.levelTaskArg(arg.level)
		);
		i(".MeasurementTaskArg",
			() -> MeasurementTaskArg.create(),
			(arg, widget) -> {
				widget.add(wComboBox(arg.measurementType()));
			},
			arg -> taskMsg.measurementTaskArg(enumMsg.valueName(arg.measurementType()))
		);
		i(".ProgramTaskArg",
			() -> ProgramTaskArg.create(),
			(arg, widget) -> {
				widget.add(wIntegerBox(arg.startDuration(0)));
				widget.add(wFloatBox(arg.startLevel(0)));
				widget.add(wIntegerBox(arg.endDuration(0)));
			},
			arg -> taskMsg.programTaskArg(taskMsg.program(arg.startDuration[0], arg.startLevel[0], arg.endDuration[0]))
		);
		i(".ValueTaskArg",
			() -> ValueTaskArg.create(),
			(arg, widget) -> {
				widget.add(wComboBox(arg.measurementType()));
				widget.add(wFloatBox(arg.value()));
				widget.add(wTextBox(arg.substance()));
			},
			arg -> 	(arg.measurementType == null) ? taskMsg.valueTaskArg() 
					: ((arg.value < -999) 	? ( isEmpty(arg.substance) 	? taskMsg.valueUnitTaskArg(enumMsg.valueName(arg.measurementType()))
																		: taskMsg.valueUnitSubstanceTaskArg(enumMsg.valueName(arg.measurementType()),arg.substance))
											: ( isEmpty(arg.substance) 	? taskMsg.valueValueTaskArg(arg.measurementType().get().getUnit().toString(arg.value))
																		: taskMsg.valueValueSubstanceTaskArg(arg.measurementType().get().getUnit().toString(arg.value),arg.substance)))
		);
	}
	public static InheritanceTypeInfo<TaskArg> info(TaskArg taskArg) { return infos.get(taskArg.$type); }
	public static String format(TaskArg taskArg) { return info(taskArg).format(taskArg); }
}
