package jesperl.dk.smoothieaq.server.noserver;

import java.util.concurrent.*;

import rx.*;
import rx.subjects.*;

public class WireTest {
	
	public static void main(String[] args) throws InterruptedException {
		Observable<Long> interval1 = Observable.interval(1, TimeUnit.SECONDS);
		Observable<Long> interval2 = Observable.interval(1, TimeUnit.SECONDS);
		
		Subject<String,String> mux = new SerializedSubject<String, String>(BehaviorSubject.create());
		
		Thread.sleep(1000);
		interval1.map(l -> "a"+l).subscribe(mux);
		Thread.sleep(1000);
		interval2.map(l -> "b"+l).subscribe(mux);
		Thread.sleep(2000);
		mux.subscribe(s -> System.out.println("x"+s));
		Thread.sleep(2000);
		mux.subscribe(s -> System.out.println("y"+s));
		Thread.sleep(5000);
		
	}

}
