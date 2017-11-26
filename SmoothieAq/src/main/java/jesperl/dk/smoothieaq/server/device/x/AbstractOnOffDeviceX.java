package jesperl.dk.smoothieaq.server.device.x;

import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;

public abstract class AbstractOnOffDeviceX extends AbstractDeviceX implements OnOffDeviceX {

	public AbstractOnOffDeviceX(String description, MeasurementType measurementType, DeviceType deviceClasse, DeviceClass deviceType) {
		super(description, measurementType, deviceClasse, deviceType);
	}

}
