package jesperl.dk.smoothieaq.client.inheritancetypes;

import static jesperl.dk.smoothieaq.client.context.CContext.*;
import static jesperl.dk.smoothieaq.client.text.TaskMessages.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.util.*;
import java.util.function.*;

import static jesperl.dk.smoothieaq.client.components.GuiUtil.*;

import gwt.material.design.client.base.*;
import jesperl.dk.smoothieaq.shared.model.schedule.*;

public class ScheduleInfo {
	
	@SuppressWarnings("unchecked") private static <T extends Schedule> void i(String type, Supplier<T> createNew, BiConsumer<T, MaterialWidget> addFields, Function<T, String> format) {
		infos.put(type, new InheritanceTypeInfo<Schedule>((Supplier<Schedule>)createNew, (BiConsumer<Schedule, MaterialWidget>)addFields, (Function<Schedule,String>)format));
	}
	
	static public String scheduleTime(ScheduleTime scheduleTime) {
		if (scheduleTime == null || (scheduleTime.hour == 0 && scheduleTime.minute == 0)) return "";
		return taskMsg.scheduleTime(scheduleTime.hour, scheduleTime.minute);
	}
	static public String scheduleLength(ScheduleTime scheduleTime) {
		return taskMsg.scheduleLength(scheduleTime.hour, scheduleTime.minute);
	}
	static public List<String> weekDays(boolean[] weekDayFlags) {
		List<String> weekDays = new ArrayList<>();
		if (weekDayFlags != null)
			for (int i = 0; i < weekDayFlags.length; i++) weekDays.add(taskMsg.weekDay(i));
		return weekDays;
	}
	static public String everyNMonthsX(EveryNMonths everyNMonths) {
		return taskMsg.everyNMonthsX(everyNMonths.dayInMonth, everyNMonths.atSpecifiDay, taskMsg.everyNMonthsWeekDays(weekDays(everyNMonths.atWeekDays)));
	}
	static public String format(Schedule schedule) { return info(schedule).format(schedule); }
	
	public static final Map<String, InheritanceTypeInfo<Schedule>> infos = new HashMap<>();
	static {
		i(".EveryNDays",
			() -> Schedule_HelperInheritace.createEveryNDays(),
			(arg, widget) -> {
				widget.add(wShortBox(arg.days()));
			},
			arg -> taskMsg.everyNDays(arg.days, scheduleTime(arg.atTime))
		);
		i(".EveryNHours",
			() -> Schedule_HelperInheritace.createEveryNHours(),
			(arg, widget) -> {
			},
			arg -> taskMsg.everyNHours(arg.hours, arg.atMinute)
		);
		i(".EveryNMonths",
			() -> Schedule_HelperInheritace.createEveryNMonths(),
			(arg, widget) -> {
			},
			arg -> taskMsg.everyNMonths(arg.months, everyNMonthsX(arg))
		);
		i(".EveryNWeeks",
			() -> Schedule_HelperInheritace.createEveryNWeeks(),
			(arg, widget) -> {
			},
			arg -> taskMsg.everyNWeeks(arg.weeks, weekDays(arg.atWeekDays))
		);
		i(".IntervalAllways",
			() -> Schedule_HelperInheritace.createIntervalAllways(),
			(arg, widget) -> {
			},
			arg -> taskMsg.intervalAllways()			
		);
		i(".IntervalEndLength",
			() -> Schedule_HelperInheritace.createIntervalEndLength(),
			(arg, widget) -> {
			},
			arg -> taskMsg.intervalEndLength(format(arg.end), scheduleLength(arg.length))
		);
		i(".IntervalEqualTo",
			() -> Schedule_HelperInheritace.createIntervalEqualTo(),
			(arg, widget) -> {
			},
			arg -> taskMsg.intervalEqualTo(ctx.cDevices.name(arg.equalToDeviceId))
		);
		i(".IntervalInverseTo",
			() -> Schedule_HelperInheritace.createIntervalInverseTo(),
			(arg, widget) -> {
			},
			arg -> taskMsg.intervalInversTo(ctx.cDevices.name(arg.inverseToDeviceId))
		);
		i(".IntervalStartEnd",
			() -> Schedule_HelperInheritace.createIntervalStartEnd(),
			(arg, widget) -> {
			},
			arg -> taskMsg.intervalStartEnd(format(arg.start), format(arg.end))
		);
		i(".IntervalStartLength",
			() -> Schedule_HelperInheritace.createIntervalStartLength(),
			(arg, widget) -> {
			},
			arg -> taskMsg.intervalStartLength(format(arg.start), scheduleLength(arg.length))
		);
		i(".PointAtDayAbsolute",
			() -> Schedule_HelperInheritace.createPointAtDayAbsolute(),
			(arg, widget) -> {
			},
			arg -> taskMsg.pointAtDayAbsolute(scheduleTime(arg.atTime), weekDays(arg.weekDays))
		);
		i(".PointEqualTo",
			() -> Schedule_HelperInheritace.createPointEqualTo(),
			(arg, widget) -> {
			},
			arg -> taskMsg.pointEqualTo(ctx.cDevices.name(arg.equalToDeviceId))
		);
		i(".PointEqualToEnd",
			() -> Schedule_HelperInheritace.createPointEqualToEnd(),
			(arg, widget) -> {
			},
			arg -> taskMsg.pointEqualToEnd(ctx.cDevices.name(arg.equalToDeviceId))
		);
		i(".PointNever",
			() -> Schedule_HelperInheritace.createPointNever(),
			(arg, widget) -> {
			},
			arg -> taskMsg.pointNever()
		);
		i(".PointRelative",
			() -> Schedule_HelperInheritace.createPointRelative(),
			(arg, widget) -> {
			},
			arg -> (arg.beginning) 	? (arg.earlier 	? taskMsg.pointRelativeStartEarlier(scheduleLength(arg.shiftTime), ctx.cDevices.name(arg.relativeToDeviceId))
													: taskMsg.pointRelativeStartLater(scheduleLength(arg.shiftTime), ctx.cDevices.name(arg.relativeToDeviceId)))
									: (arg.earlier 	? taskMsg.pointRelativeStopEarlier(scheduleLength(arg.shiftTime), ctx.cDevices.name(arg.relativeToDeviceId))
													: taskMsg.pointRelativeStopLater(scheduleLength(arg.shiftTime), ctx.cDevices.name(arg.relativeToDeviceId)))
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
