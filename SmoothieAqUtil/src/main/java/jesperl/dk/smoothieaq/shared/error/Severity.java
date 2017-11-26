package jesperl.dk.smoothieaq.shared.error;

public enum Severity {
	info(10),
	minor(20),
	medium(30),
	major(40),
	fatal(50),
	;

	private int id;
	
	private Severity(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}