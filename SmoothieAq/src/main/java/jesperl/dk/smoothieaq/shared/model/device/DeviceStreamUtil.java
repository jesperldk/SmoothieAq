package jesperl.dk.smoothieaq.shared.model.device;

import static jesperl.dk.smoothieaq.shared.model.device.DeviceStream.*;
import static jesperl.dk.smoothieaq.shared.model.device.DeviceStream.onoff;
import static jesperl.dk.smoothieaq.shared.model.device.DeviceStreamType.*;
import static jesperl.dk.smoothieaq.shared.model.measure.MeasurementType.*;

import java.util.*;

import jesperl.dk.smoothieaq.shared.model.measure.*;

public abstract class DeviceStreamUtil {

	public static Map<DeviceStream, DeviceStreamType> toType = new HashMap<>();
	{
		toType.put(startstopX,eventStream); 
		toType.put(onoff,continousStream); 
		toType.put(level,continousStream);
		toType.put(watt,continousStream);
		toType.put(pctlevel,continousStream);
		toType.put(pgmX,eventStream);
		toType.put(sofar,continousStream);
		toType.put(amountX,eventStream);
		toType.put(capacity,continousStream);
		toType.put(measureX,eventStream);
		toType.put(error,continousStream);
		toType.put(alarm,continousStream);
		toType.put(duetask,continousStream);
	}

	public static Map<DeviceStream, MeasurementType> toMesurementType = new HashMap<>();
	{
		toMesurementType.put(startstopX,MeasurementType.onoff); 
		toMesurementType.put(onoff,status); 
		toMesurementType.put(level,otherMeasure);
		toMesurementType.put(watt,energyConsumption);
		toMesurementType.put(pctlevel,change);
		toMesurementType.put(pgmX,otherMeasure); // --> time
		toMesurementType.put(sofar,otherMeasure);
		toMesurementType.put(amountX,otherMeasure);
		toMesurementType.put(capacity,otherMeasure);
		toMesurementType.put(measureX,otherMeasure);
		toMesurementType.put(error,MeasurementType.onoff);
		toMesurementType.put(alarm,MeasurementType.onoff);
		toMesurementType.put(duetask,MeasurementType.onoff);
	}
}