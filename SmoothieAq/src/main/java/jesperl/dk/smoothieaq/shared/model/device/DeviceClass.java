package jesperl.dk.smoothieaq.shared.model.device;

public enum DeviceClass { 
	sensor(1), 
	onoff(2), 
	level(3),
	toggle(4),
	doser(5),
	status(6),
	container(7),
	calculated(8),
	manual(98),
	no(99),
	;

	private int id;
	
	private DeviceClass(int id) {
		this.id = id; 
	}
	
	public int getId() {
		return id;
	}
}