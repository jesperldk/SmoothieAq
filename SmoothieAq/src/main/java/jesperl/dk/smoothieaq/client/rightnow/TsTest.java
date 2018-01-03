package jesperl.dk.smoothieaq.client.rightnow;

import java.util.*;

import jesperl.dk.smoothieaq.client.timeseries.*;
import rx.Observable;

public class TsTest extends TsFull<TsElement> {
	
	@SuppressWarnings("deprecation")
	public TsTest(int size) {
		super(generate(size, new Date(117,2,2)),Observable.never());
	}
	
	public static TsElement[] generate(int size, Date oldest) {
		TsElement[] full = new TsElement[size];
		long stamp = oldest.getTime()+1200L*60*60*1000L;
		for (int i = 0; i < size; i++) {
			stamp += 60*60*1000;
			TsElement element = new TsElement();
			element.stamp = stamp;
			element.deviceId = (int) ((stamp / 60*60*1000) % 10);
			element.data = new Integer(i);
			full[i] = element;
		}
		return full;
	}

}
