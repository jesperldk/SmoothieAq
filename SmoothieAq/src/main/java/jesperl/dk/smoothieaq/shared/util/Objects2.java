package jesperl.dk.smoothieaq.shared.util;

import java.util.stream.*;

import rx.*;

public class  Objects2 {
	
	static public <T> Observable<T> observable(Stream<T> stream) { return Observable.from(stream::iterator); }
	static public <T> Observable<T> only(T value) { return Observable.just(value).concatWith(Observable.never()); }
}
