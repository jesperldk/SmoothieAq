package jesperl.dk.smoothieaq.client.text;

import java.util.*;

import com.google.gwt.core.client.*;
import com.google.gwt.i18n.client.*;

import jesperl.dk.smoothieaq.shared.model.schedule.*;

public interface TaskMessages extends Messages {
	public static TaskMessages taskMsg = GWT.create(TaskMessages.class );
	
	@DefaultMessage("at {0,number,00}:{1,number,00}")
	public String scheduleTime(int hours, int minutes);

	@DefaultMessage("{0,number,00}:{1,number,00} hours")
	@AlternateMessage({"=0","{1} minutes"})
	public String scheduleLength(@PluralCount int hours, int minutes);

	@DefaultMessage("every {0} days{1}")
	@AlternateMessage({"=1","every day{1}"})
	public String everyNDays(@PluralCount int n, @Optional String at);

	@DefaultMessage("every {0} hours at :{1,number,00}")
	@AlternateMessage({"=1","every hour at :{1,number,00}"})
	public String everyNHours(@PluralCount int n, int atMinute);

	@DefaultMessage("every {0} minutes")
	@AlternateMessage({"=1","every minute"})
	public String everyNMinutes(@PluralCount int n);

	@DefaultMessage("every {0} months at {1}")
	@AlternateMessage({"=1","every month at {1}"})
	public String everyNMonths(@PluralCount int n, String at);

	@DefaultMessage("everyNMonthsX-format-missing")
	@AlternateMessage({	"specific",		"the {1}.",
						"firstWeekDay",	"the first {2}",
						"lastWeekDay",	"the last {2}"})
	public String everyNMonthsX(@Select ScheduleDayInMonth x, @Optional int atDay, @Optional String weekdays);
	
	@DefaultMessage("of {0,list}")
	@AlternateMessage({"=1","{0,list}"})
	public String everyNMonthsWeekDays(@PluralCount List<String> weekdays);
	
	@DefaultMessage("weekDay-format-missing{0}")
	@AlternateMessage({	"=0","Monday","=1","Tuesday","=2","Wednesday","=3","Thursday","=4","Friday","=5","Saturday","=6","Sunday"})
	public String weekDay(@PluralCount int dayNo);
	
	@DefaultMessage("every {0} weeks at {1,list}")
	@AlternateMessage({"=1","every week at {1,list}"})
	public String everyNWeeks(@PluralCount int n, List<String> at);
	
	@DefaultMessage("allways on")
	public String intervalAllways();

	@DefaultMessage("stop {0} and start {1} before that")
	public String intervalEndLength(String point, String length);

	@DefaultMessage("start and stop the same as {0}")
	public String intervalEqualTo(String device);

	@DefaultMessage("start when {0} stops and stop when it starts")
	public String intervalInversTo(String device);

	@DefaultMessage("start {0} and stop {1}")
	public String intervalStartEnd(String start, String stop);

	@DefaultMessage("start {0} and stop after {1}")
	public String intervalStartLength(String point, String length);
	
	@DefaultMessage("{0} every {1,list}")
	@AlternateMessage({	"=0",	"At {0}",
						"=1",	"At {0} on {1,list}"})
	public String pointAtDayAbsolute(String time, @PluralCount @Optional List<String> weekday);

	@DefaultMessage("at the start of {0}")
	public String pointEqualTo(String device);

	@DefaultMessage("at the stopping of {0}")
	public String pointEqualToEnd(String device);

	@DefaultMessage("never")
	public String pointNever();

	@DefaultMessage("{0} earlier than the start of {1}")
	public String pointRelativeStartEarlier(String length, String device);

	@DefaultMessage("{0} later than the start of {1}")
	public String pointRelativeStartLater(String length, String device);

	@DefaultMessage("{0} earlier than the stopping of {1}")
	public String pointRelativeStopEarlier(String length, String device);

	@DefaultMessage("{0} later than the stopping of {1}")
	public String pointRelativeStopLater(String length, String device);
	
	
	@DefaultMessage("do {0}")
	public String descriptionTaskArg(String description);
	
	@DefaultMessage("set level to {0}")
	public String levelTaskArg(Float level);
	
	@DefaultMessage("take a measurement of {0}")
	public String measurementTaskArg(String measurementType);
	
	@DefaultMessage("use the program ''{0}''")
	public String programTaskArg(String program);
	
	@DefaultMessage("at start, ramp up to level {1} over {0} minutes; {2} minutes befor stop, start ramping down to level 0")
	public String program(int startDuration, float level, int stopDuration);
	
	@DefaultMessage("{0} of {1}")
	public String valueValueSubstanceTaskArg(String valueWithUnit, String substance);
	
	@DefaultMessage("some {0} of {1}")
	public String valueUnitSubstanceTaskArg(String measurementType, String substance);
	
	@DefaultMessage("{0}")
	public String valueValueTaskArg(String valueWithUnit);
	
	@DefaultMessage("some {0}")
	public String valueUnitTaskArg(String measurementType);
	
	@DefaultMessage("some amount")
	public String valueTaskArg();
	
	@DefaultMessage("when ''{0}''")
	public String when(String when);
	
	
	@DefaultMessage("postponed to {0}")
	public String postponedTo(String stamp);
	
	@DefaultMessage("this task is due, and have been waiting since {0}!")
	public String waitingFrom(String stamp);
	
	@DefaultMessage("next scheduled for {0}")
	public String nextStart(String stamp);
	
	@DefaultMessage("is currently started, and will stop at {0}")
	public String nextEnd(String stamp);
	
	@DefaultMessage("is not scheduled")
	public String noNext();
	
	@DefaultMessage(", it was last started at {0}")
	public String lastStart(String stamp);
	
}
