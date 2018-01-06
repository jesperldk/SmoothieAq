package jesperl.dk.smoothieaq.client.timeseries;

import jesperl.dk.smoothieaq.shared.model.event.*;

public class TsMeasurement extends TsElement {
	public float value;
	
	public TsMeasurement() {}
	
	public TsMeasurement(ME me) {
		this.type = 1; // TODO
		this.stamp = me.t;
		this.deviceId = me.i/256;
		this.streamId = me.i % 256;
		this.value = me.v;
	}
}
