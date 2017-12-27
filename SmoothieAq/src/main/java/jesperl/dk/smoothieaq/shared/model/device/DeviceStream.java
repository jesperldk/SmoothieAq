package jesperl.dk.smoothieaq.shared.model.device;

public enum DeviceStream { 
	startstopX(1), 
	onoff(2), 
	level(3),
	watt(4),
	pctlevel(4),
	pgmX(5),
	sofar(6),
	amountX(7),
	capacity(8),
	measureX(9),
	error(100),
	alarm(101),
	duetask(102)
	;

	private int id;
	
	private DeviceStream(int id) { this.id = id; }
	
	public int getId() { return id; }
	public DeviceStreamType getType() { return DeviceStreamUtil.toType.get(this); }
}