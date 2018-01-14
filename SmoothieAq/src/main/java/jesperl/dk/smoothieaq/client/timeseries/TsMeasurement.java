package jesperl.dk.smoothieaq.client.timeseries;

import java.util.*;

import jesperl.dk.smoothieaq.shared.model.event.*;

public class TsMeasurement extends TsElement {
	public float value;
	
	public TsMeasurement() {
		this.type = 1; // TODO
	}
	
	public TsMeasurement(int deviceId, short streamId, float value) {
		this();
		this.stamp = new Date().getTime();
		this.deviceId = deviceId;
		this.streamId = streamId;
		this.value = value;
	}
	
	public TsMeasurement(ME me) {
		this();
		this.stamp = me.t;
		this.deviceId = (short) (me.i/256);
		this.streamId = (short) (me.i % 256);
		this.value = me.v;
	}
}
