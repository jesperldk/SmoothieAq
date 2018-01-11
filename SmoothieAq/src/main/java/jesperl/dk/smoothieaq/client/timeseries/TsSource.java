package jesperl.dk.smoothieaq.client.timeseries;

import rx.*;
import rx.functions.*;

public interface TsSource<E> {

	Observable<E> elementsFrom(long from, int preCount, int postCount, Func1<E,Boolean> predicate);
	Observable<E> newElements(Func1<E,Boolean> predicate);
	Observable<Void> refreshListen();
	void release();
	
	default TsSource<E> filter(Func1<E,Boolean> predicate) {
		TsSource<E> s = this;
		return new TsSource<E>() {
			@Override public Observable<E> elementsFrom(long from, int preCount, int postCount, Func1<E,Boolean> predicate2) {
				return s.elementsFrom(from, preCount, postCount, predicate).filter(predicate);
			}
			@Override public Observable<E> newElements(Func1<E,Boolean> predicate2) {
				return s.newElements(predicate).filter(predicate);
			}
			@Override public Observable<Void> refreshListen() {
				return s.refreshListen();
			}
			@Override public void release() { s.release(); }
		};
	}
	
	default <E2> TsSource<E2> map(Func1<E,E2> mapFuc) {
		Func1<E,E2> nnmapFuc = e -> e == null ? null : mapFuc.call(e);
		TsSource<E> s = this;
		return new TsSource<E2>() {
			@Override public Observable<E2> elementsFrom(long from, int preCount, int postCount, Func1<E2,Boolean> predicate) {
				Observable<E2> o = s.elementsFrom(from, preCount, postCount, null).map(nnmapFuc);
				if (predicate != null) o = o.filter(predicate);
				return o;
			}
			@Override public Observable<E2> newElements(Func1<E2,Boolean> predicate) {
				Observable<E2> o = s.newElements(null).map(nnmapFuc);
				if (predicate != null) o = o.filter(predicate);
				return o;
			}
			@Override public Observable<Void> refreshListen() {
				return s.refreshListen();
			}
			@Override public void release() { s.release(); }
		};
	}
}
