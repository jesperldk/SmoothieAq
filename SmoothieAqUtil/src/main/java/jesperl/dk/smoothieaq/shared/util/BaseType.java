package jesperl.dk.smoothieaq.shared.util;

public enum BaseType {
	notype(0),
	integer(10),
	floating(20),
	string(30),
	;

	private int id;
	
	private BaseType(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}