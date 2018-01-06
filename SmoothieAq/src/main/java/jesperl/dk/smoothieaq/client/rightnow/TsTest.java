package jesperl.dk.smoothieaq.client.rightnow;

import java.util.*;

import jesperl.dk.smoothieaq.client.timeseries.*;
import rx.Observable;

public class TsTest extends TsFull<TsMeasurement> {
	
	@SuppressWarnings("deprecation")
	public TsTest(int size) {
		super(generate(size, new Date(117,2,2)),Observable.never());
	}
	
	public static TsMeasurement[] generate(int size, Date oldest) {
		TsMeasurement[] full = new TsMeasurement[size];
		long stamp = oldest.getTime()+1200L*60*60*1000L;
		for (int i = 0; i < size; i++) {
			stamp += 60*60*1000;
			TsMeasurement element = new TsMeasurement();
			element.stamp = stamp;
			element.deviceId = (int) ((stamp / 60*60*1000) % 10);
			element.value = i;
			full[i] = element;
		}
		return full;
	}

}
