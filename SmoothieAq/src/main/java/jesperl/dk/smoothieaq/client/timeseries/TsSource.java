package jesperl.dk.smoothieaq.client.timeseries;

import rx.*;
import rx.functions.*;

public interface TsSource<E> {

	Observable<E> elementsFrom(long from, int preCount, int postCount);
	Observable<E> newElements();
	Observable<Void> refreshListen();
	
	default TsSource<E> filter(Func1<E,Boolean> predicate) {
		TsSource<E> s = this;
		return new TsSource<E>() {
			@Override public Observable<E> elementsFrom(long from, int preCount, int postCount) {
				return s.elementsFrom(from, preCount, postCount).filter(predicate);
			}
			@Override public Observable<E> newElements() {
				return s.newElements().filter(predicate);
			}
			@Override public Observable<Void> refreshListen() {
				return s.refreshListen();
			}
		};
	}
	
	default <E2> TsSource<E2> map(Func1<E,E2> mapFuc) {
		TsSource<E> s = this;
		return new TsSource<E2>() {
			@Override public Observable<E2> elementsFrom(long from, int preCount, int postCount) {
				return s.elementsFrom(from, preCount, postCount).map(mapFuc);
			}
			@Override public Observable<E2> newElements() {
				return s.newElements().map(mapFuc);
			}
			@Override public Observable<Void> refreshListen() {
				return s.refreshListen();
			}
		};
	}
}
