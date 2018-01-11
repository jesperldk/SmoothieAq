package jesperl.dk.smoothieaq.client.components.tstable;

import jesperl.dk.smoothieaq.client.context.*;
import jesperl.dk.smoothieaq.client.timeseries.*;

public class TsMeasurementRowData extends TsElementRowData {
	
	private String value;
	
	public TsMeasurementRowData(TsMeasurement measurement) {
		super(measurement);
		value = CContext.ctx.cDevices.formatter(element.deviceId,element.streamId).apply(measurement.value);
	}
	
	@Override public String text() { return value; }
}
