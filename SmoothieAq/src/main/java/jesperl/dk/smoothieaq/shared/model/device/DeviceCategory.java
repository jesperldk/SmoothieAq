package jesperl.dk.smoothieaq.shared.model.device;

public enum DeviceCategory { 
	primary(1), 
	secondary(2), 
	system(3),
	external(4),
	manual(5),
	;

	private int id;
	
	private DeviceCategory(int id) {
		this.id = id;
	}
	 
	public int getId() {
		return id;
	}
}