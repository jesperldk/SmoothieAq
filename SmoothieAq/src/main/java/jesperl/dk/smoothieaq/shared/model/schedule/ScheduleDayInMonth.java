package jesperl.dk.smoothieaq.shared.model.schedule;

public enum ScheduleDayInMonth {
	specific(1), weekDay(2), workDay(3), weekendDay(4);
	
	private int id;
	
	private ScheduleDayInMonth(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}
