package jesperl.dk.smoothieaq.shared.model.device;

public enum DeviceStatusChange { 
	enable(1), disable(2), delete(3), pause(4), unpause(5);
	
	private int id;
	
	private DeviceStatusChange(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}