package jesperl.dk.smoothieaq.shared.model.device;

public enum DeviceStream { 
	onoff(1,true), 
	startstopX(2,false), 
	level(3,true),
	levelX(4,false),
	pct(5,true),
	doseX(6,false),
	soFar(7,true),
	watt(8,true),
	;

	private int id;
	private boolean behavior;
	
	private DeviceStream(int id, boolean behavior) {
		this.id = id;
		this.behavior = behavior;
	}
	
	public int getId() { return id; }
	public boolean isBehavior() { return behavior; }
}