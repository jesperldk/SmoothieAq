package jesperl.dk.smoothieaq.shared.model.device;

public enum DeviceStatusType { 
	enabled(1), disabled(2), deleted(3), paused(4), stopped(5);
	
	private int id;
	
	private DeviceStatusType(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}