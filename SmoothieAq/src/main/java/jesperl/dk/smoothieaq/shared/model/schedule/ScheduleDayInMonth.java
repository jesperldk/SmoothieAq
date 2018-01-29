package jesperl.dk.smoothieaq.shared.model.schedule;

public enum ScheduleDayInMonth {
	specific(1), firstWeekDay(2), lastWeekDay(3);
	
	public final int id;
	
	private ScheduleDayInMonth(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}
 