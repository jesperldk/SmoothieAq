package jesperl.dk.smoothieaq.shared.util;

import java.util.*;
import java.util.stream.*;

import rx.*;
import rx.Observable;
import rx.functions.*;

public class  Objects2 {
	
	static public <T> Observable<T> observable(Stream<T> stream) { return Observable.from(stream::iterator); }
	static public <T> Observable<T> only(T value) { return Observable.just(value).concatWith(Observable.never()); }
	
	static public <T> Stream<T> stream(Observable<T> observable) { return observable.toList().toBlocking().single().stream(); }
	
	static public Subscription unsubscribe(Subscription subscription) { if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe(); return null; }
	
	@SuppressWarnings("serial")
	static public class Subscriptions extends HashSet<Subscription> {
		public <T> void subscripe(Observable<T> o, Action1<? super T> c) { add(o.subscribe(c)); }
		public void unsubscripe() {
			forEach(s -> unsubscribe(s));
			clear();
		}
	}
}
