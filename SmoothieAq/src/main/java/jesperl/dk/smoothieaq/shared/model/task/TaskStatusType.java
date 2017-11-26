package jesperl.dk.smoothieaq.shared.model.task;

public enum TaskStatusType {
	
	enabled(1), disabled(2), deleted(3);
	
	private int id;
	
	private TaskStatusType(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}
