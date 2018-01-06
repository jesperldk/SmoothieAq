package jesperl.dk.smoothieaq.client.components.tstable;

import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import jesperl.dk.smoothieaq.client.timeseries.*;

public class TsMeasurementRowData extends TsElementRowData {
	
	private String value;
	
	public TsMeasurementRowData(TsMeasurement measurement) {
		super(measurement);
		value = strv(measurement.value);
	}
	
	@Override public String text() { return value; }
}
