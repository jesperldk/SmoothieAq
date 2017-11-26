package jesperl.dk.smoothieaq.shared.model.device;

public enum DeviceDependencyType { 
	none(1), onlyOnWhenOn(2), sameAs(3), sameAsSmoothie(4);
	
	private int id;
	
	private DeviceDependencyType(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	} 
}