package jesperl.dk.smoothieaq.server.device.x;

import jesperl.dk.smoothieaq.shared.model.device.*;

public class GenericOnOffDeviceX extends AbstractOnOffDeviceX {

	public GenericOnOffDeviceX() {
		super("Generic", null, DeviceType.generic, DeviceClass.onoff);
	}

}
