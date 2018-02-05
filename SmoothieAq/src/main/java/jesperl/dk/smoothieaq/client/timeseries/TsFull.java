package jesperl.dk.smoothieaq.client.timeseries;

import static java.lang.Math.*;

import rx.*;
import rx.functions.*;
import rx.subjects.*;

public class TsFull<E extends TsElement> implements TsSource<E> {
	
	private E[] buf;
	Observable<E> newElements;
	Subject<Void, Void> refresh = PublishSubject.create();
	
	public TsFull(E[] full, Observable<E> newElements) {
		this.buf = full;
		this.newElements = newElements;
	}

	public void refresh(E[] full) {
		this.buf = full;
		refresh.onNext(null);
	}

	@Override public Observable<E> elementsFrom(long fromNewestNotIncl, int countNewer, int countOlder, Func1<E,Boolean> predicate) {
		return Observable.unsafeCreate(s -> {
			long frm = (fromNewestNotIncl == 0) ? Long.MAX_VALUE : fromNewestNotIncl;
			int newer = countNewer;
			int older = countOlder;
			@SuppressWarnings("unchecked") E[] lookback = (E[]) new TsElement[newer];
			int lookbackp = 0;
			
			int p = buf.length-1;
			while (p >= 0 && buf[p].stamp >= frm) {
				if (newer > 0 && filter(predicate,buf[p])) { lookback[lookbackp % countNewer] = buf[p]; lookbackp++; }
				p--;
			}
			if (p < 0 && buf.length > 0) p = 0;
			
			while (newer > lookbackp) { if (s.isUnsubscribed()) return; s.onNext(null); newer--; }
			lookbackp = max(0,lookbackp-newer);
			while (newer > 0) { if (s.isUnsubscribed()) return; 
				newer--;
				s.onNext(lookback[lookbackp % countNewer]);
				lookbackp++;
			}
			while (p >= 0 && older > 0) { if (s.isUnsubscribed()) return; if (filter(predicate,buf[p])) { s.onNext(buf[p]); p--; older--; } }
			while (older > 0) { if (s.isUnsubscribed()) return; s.onNext(null); older--; }
//			@SuppressWarnings("unchecked") E[] lookback = (E[]) new TsElement[older];
//			int lookbackp = 0;
//			
//			int p = 0;
//			while (p < buf.length && buf[p].stamp < frm) {
//				if (older > 0 && filter(predicate,buf[p])) { lookback[lookbackp % countOlder] = buf[p]; lookbackp++; }
//				p++;
//			}
//			if (p >= buf.length && buf.length > 0) p = buf.length-1;
//			
//			while (older > lookbackp) { if (s.isUnsubscribed()) return; s.onNext(null); older--; }
//			lookbackp = max(0,lookbackp-older);
//			while (older > 0) { if (s.isUnsubscribed()) return; 
//				older--;
//				s.onNext(lookback[lookbackp % countOlder]);
//				lookbackp++;
//			}
//			while (p < buf.length && newer > 0) { if (s.isUnsubscribed()) return; if (filter(predicate,buf[p])) { s.onNext(buf[p]); p++; newer--; } }
//			while (newer > 0) { if (s.isUnsubscribed()) return; s.onNext(null); newer--; }
			s.onCompleted();
		});
	}
	
	private boolean filter(Func1<E,Boolean> predicate, E e) { return predicate == null || predicate.call(e); }

	@Override public Observable<E> newElements(Func1<E,Boolean> predicate) {
		return newElements.filter(predicate);
	}

	@Override public Observable<Void> refreshListen() {
		return refresh;
	}

	@Override public void release() {
		refresh.onCompleted();
	}

}
