package jesperl.dk.smoothieaq.client.inheritancetypes;

import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.util.*;
import java.util.function.*;

import static jesperl.dk.smoothieaq.client.components.GuiUtil.*;

import gwt.material.design.client.base.*;
import jesperl.dk.smoothieaq.shared.model.schedule.*;

public class ScheduleInfo {
	public static final Map<String, InheritanceTypeInfo<Schedule>> infos = new HashMap<>();
	
	@SuppressWarnings("unchecked") private static <T extends Schedule> void i(String type, Supplier<T> createNew, BiConsumer<T, MaterialWidget> addFields) {
		infos.put(type, new InheritanceTypeInfo<Schedule>((Supplier<Schedule>)createNew, (BiConsumer<Schedule, MaterialWidget>)addFields));
	}
	static {
		i(".EveryNDays",
			() -> Schedule_HelperInheritace.createEveryNDays(),
			(arg, widget) -> {
				widget.add(wShortBox(arg.days()));
			}
		);
		i(".EveryNHours",
			() -> Schedule_HelperInheritace.createEveryNHours(),
			(arg, widget) -> {
			}
		);
		i(".EveryNMonths",
			() -> Schedule_HelperInheritace.createEveryNMonths(),
			(arg, widget) -> {
			}
		);
		i(".EveryNWeeks",
			() -> Schedule_HelperInheritace.createEveryNWeeks(),
			(arg, widget) -> {
			}
		);
		i(".IntervalAllways",
			() -> Schedule_HelperInheritace.createIntervalAllways(),
			(arg, widget) -> {
			}
		);
		i(".IntervalEndLength",
			() -> Schedule_HelperInheritace.createIntervalEndLength(),
			(arg, widget) -> {
			}
		);
		i(".IntervalEqualTo",
			() -> Schedule_HelperInheritace.createIntervalEqualTo(),
			(arg, widget) -> {
			}
		);
		i(".IntervalInverseTo",
			() -> Schedule_HelperInheritace.createIntervalInverseTo(),
			(arg, widget) -> {
			}
		);
		i(".IntervalStartEnd",
			() -> Schedule_HelperInheritace.createIntervalStartEnd(),
			(arg, widget) -> {
			}
		);
		i(".IntervalStartLength",
			() -> Schedule_HelperInheritace.createIntervalStartLength(),
			(arg, widget) -> {
			}
		);
		i(".PointAtDayAbsolute",
			() -> Schedule_HelperInheritace.createPointAtDayAbsolute(),
			(arg, widget) -> {
			}
		);
		i(".PointEqualTo",
			() -> Schedule_HelperInheritace.createPointEqualTo(),
			(arg, widget) -> {
			}
		);
		i(".PointEqualToEnd",
			() -> Schedule_HelperInheritace.createPointEqualToEnd(),
			(arg, widget) -> {
			}
		);
		i(".PointNever",
			() -> Schedule_HelperInheritace.createPointNever(),
			(arg, widget) -> {
			}
		);
		i(".PointRelative",
			() -> Schedule_HelperInheritace.createPointRelative(),
			(arg, widget) -> {
			}
		);
	}
	public static InheritanceTypeInfo<Schedule> info(Schedule schedule) { return infos.get(schedule.$type); }
	
	public static final Set<String> autoSchedules = 
			set(".IntervalAllways",".IntervalEndLenght",".IntervalEqualTo",".IntervalInverseTo",".IntervalStartEnd",".IntervalStartLength");
	public static final Set<String> autoSchedulePointss = 
			set(".PointAtDayAbsolute",".PointRelative",".PointEqualTo",".PointEqualToEnd");
	public static final Set<String> autoPoints = 
			set(".EveryNDays",".EveryNHours",".PointAtDayAbsolute",".PointRelative",".PointNever");
	public static final Set<String> manualPoints = 
			set(".EveryNDays",".EveryNMonths",".EveryNWeeks",".PointAtDayAbsolute");
}
