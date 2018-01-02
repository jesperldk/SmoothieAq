package jesperl.dk.smoothieaq.client.rightnow;

import static java.lang.Math.*;

import java.util.*;

import jesperl.dk.smoothieaq.client.timeseries.*;
import rx.Observable;

public class TsTest implements TsSource<TsElement> {
	
	private int size;
	private TsElement[] buf;
	
	public TsTest(int size) {
		this.size = size;
		buf = new TsElement[size];
		long stamp = new Date().getTime()+1200L*60*60*1000L;
		for (int i = 0; i < size; i++) {
			stamp += 60*60*1000;
			TsElement element = new TsElement();
			element.stamp = stamp;
			element.deviceId = (int) ((stamp / 60*60*1000) % 10);
			element.data = new Integer(i);
			buf[i] = element;
		}
	}

	// post is <=from and pre is > from, returned oldest first and newest last, null if no element at a given position
	@Override public Observable<TsElement> elementsFrom(long from, int preCount, int postCount) {
		return Observable.unsafeCreate(s -> {
			long frm = (from == 0) ? Long.MAX_VALUE : from;
			int pre = preCount;
			int post = postCount;
			TsElement[] lookback = new TsElement[post];
			int lookbackp = 0;
			
			int p = 0;
			while (p < size && buf[p].stamp <= frm) {
				if (post > 0) { lookback[lookbackp % postCount] = buf[p]; lookbackp++; }
				p++;
			}
			if (p >= size && size > 0) p = size -1;
			
			while (post > lookbackp) { if (s.isUnsubscribed()) return; s.onNext(null); post--; }
			lookbackp = max(0,lookbackp-postCount);
			while (post > 0) { if (s.isUnsubscribed()) return; 
				post--;
				int pp = lookbackp % postCount;
				TsElement t = lookback[pp];
				s.onNext(t);
				lookbackp++;
				}
			while (p < size && pre > 0) { if (s.isUnsubscribed()) return; s.onNext(buf[p++]); pre--; }
			while (pre > 0) { if (s.isUnsubscribed()) return; s.onNext(null); pre--; }
			s.onCompleted();
		});
	}

	@Override
	public Observable<TsElement> newElements() {
		return Observable.never();
	}

	@Override
	public Observable<Void> refreshListen() {
		return Observable.never();
	}

}
