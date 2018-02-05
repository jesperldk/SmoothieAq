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

	@Override public Observable<E> elementsFrom(long fromNewestNotIncl, int preCount, int postCount, Func1<E,Boolean> predicate) {
		return Observable.unsafeCreate(s -> {
			long frm = (fromNewestNotIncl == 0) ? Long.MAX_VALUE : fromNewestNotIncl;
			int pre = preCount;
			int post = postCount;
			@SuppressWarnings("unchecked") E[] lookback = (E[]) new TsElement[post];
			int lookbackp = 0;
			
			int p = 0;
			while (p < buf.length && buf[p].stamp < frm) {
				if (post > 0 && filter(predicate,buf[p])) { lookback[lookbackp % postCount] = buf[p]; lookbackp++; }
				p++;
			}
			if (p >= buf.length && buf.length > 0) p = buf.length-1;
			
			while (post > lookbackp) { if (s.isUnsubscribed()) return; s.onNext(null); post--; }
			lookbackp = max(0,lookbackp-post);
			while (post > 0) { if (s.isUnsubscribed()) return; 
				post--;
				s.onNext(lookback[lookbackp % postCount]);
				lookbackp++;
			}
			while (p < buf.length && pre > 0) { if (s.isUnsubscribed()) return; if (filter(predicate,buf[p])) { s.onNext(buf[p]); p++; pre--; } }
			while (pre > 0) { if (s.isUnsubscribed()) return; s.onNext(null); pre--; }
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
