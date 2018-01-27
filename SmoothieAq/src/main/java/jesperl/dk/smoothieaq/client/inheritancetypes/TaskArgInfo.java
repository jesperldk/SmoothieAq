package jesperl.dk.smoothieaq.client.inheritancetypes;

import java.util.*;
import java.util.function.*;

import static jesperl.dk.smoothieaq.client.components.GuiUtil.*;

import gwt.material.design.client.base.*;
import jesperl.dk.smoothieaq.shared.model.task.*;

public class TaskArgInfo {
	public static final Map<String, InheritanceTypeInfo<TaskArg>> infos = new HashMap<>();
	
	@SuppressWarnings("unchecked") private static <T extends TaskArg> void i(String type, Supplier<T> createNew, BiConsumer<T, MaterialWidget> addFields) {
		infos.put(type, new InheritanceTypeInfo<TaskArg>((Supplier<TaskArg>)createNew, (BiConsumer<TaskArg, MaterialWidget>)addFields));
	}
	static {
		i(".DescriptionTaskArg",
			() -> DescriptionTaskArg.create(),
			(arg, widget) -> {
				widget.add(wTextBox(arg.description()));
			}
		);
		i(".LevelTaskArg",
				() -> LevelTaskArg.create(),
				(arg, widget) -> {
					widget.add(wFloatBox(arg.level()));
				}
			);
		i(".MeasurementTaskArg",
				() -> MeasurementTaskArg.create(),
				(arg, widget) -> {
					widget.add(wComboBox(arg.measurementType()));
				}
			);
		i(".ProgramTaskArg",
				() -> ProgramTaskArg.create(),
				(arg, widget) -> {
					widget.add(wIntegerBox(arg.startDuration(0)));
					widget.add(wFloatBox(arg.startLevel(0)));
					widget.add(wIntegerBox(arg.endDuration(0)));
				}
			);
		i(".ValueTaskArg",
				() -> ValueTaskArg.create(),
				(arg, widget) -> {
					widget.add(wComboBox(arg.measurementType()));
					widget.add(wFloatBox(arg.value()));
					widget.add(wTextBox(arg.substance()));
				}
			);
	}
	public static InheritanceTypeInfo<TaskArg> info(TaskArg taskArg) { return infos.get(taskArg.$type); }
}
