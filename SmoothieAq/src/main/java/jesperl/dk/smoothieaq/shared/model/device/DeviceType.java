package jesperl.dk.smoothieaq.shared.model.device;

import static jesperl.dk.smoothieaq.shared.model.device.DeviceClass.*;

public enum DeviceType { 
	sensor(1,DeviceClass.sensor), 
	light(3,level), 
	flow(4,onoff), 
	filter(5,onoff), 
	temperatureRegulator(6,onoff), 
	heater(7,onoff), 
	chiller(8,onoff), 
	co2(9,onoff), 
	air(10,onoff), 
	uv(11,onoff), 
	doser(12,DeviceClass.doser),
	status(14,DeviceClass.status),
	button(15,toggle),
	waterLevel(16,toggle),
	topUp(17,DeviceClass.doser),
	tank(18,container),
	other(98,null), 
	generic(99,null),
	;

	private int id;
	private DeviceClass defaultClass;
	
	private DeviceType(int id, DeviceClass defaultClass) {
		this.id = id;
		this.defaultClass = defaultClass;
	}
	
	public int getId() { return id; }
	
	public DeviceClass getDefaultClass() { return defaultClass; }
}