package jesperl.dk.smoothieaq.client.context;

import static jesperl.dk.smoothieaq.client.context.CContext.*;
import static jesperl.dk.smoothieaq.client.text.MsgMessages.*;

import java.util.*;
import java.util.function.*;

import com.google.gwt.core.client.*;

import static jesperl.dk.smoothieaq.client.components.GuiUtil.*;

import jesperl.dk.smoothieaq.client.*;
import jesperl.dk.smoothieaq.client.timeseries.*;
import jesperl.dk.smoothieaq.shared.model.event.*;
import jesperl.dk.smoothieaq.util.shared.*;
import rx.*;
import rx.Observable;
import rx.Observer;
import rx.functions.*;
import rx.subjects.*;

public class CWires {
	
	private MapSetConsumer<String,Event> demux = new MapSetConsumer<>();
	private MapConsumer<Integer,TsMeasurement> measurementDemux = new MapConsumer<>();
	
	public Observable<Event> observable(String eventClass) { 
		Subject<Event, Event> eventSubject = PublishSubject.create();
		Wrap<Subscription> wsubscription = new Wrap<>();
		return eventSubject.doOnSubscribe(() -> wsubscription.wrapped = subscribe(eventClass, eventSubject)).doOnUnsubscribe(() -> wsubscription.wrapped.unsubscribe());
	}
	public Subscription subscribe(String eventClass, Observer<Event> observer) { return subscribe(eventClass, e -> observer.onNext(e)); }
	public Subscription subscribe(String eventClass, Consumer<Event> consumer) { return demux.subscribe(eventClass, consumer); }
	
	/*friend*/ Subscription subscribeMeasurement(int deviceId, short streamId, Observer<TsMeasurement> observer) { return measurementDemux.subscribe((deviceId*256+streamId), m -> observer.onNext(m)); }
	
	public void init() {
		subscribe(".ME", e -> { ME me = (ME)e; measurementDemux.accept(me.i, new TsMeasurement(me)); });
		subscribe(".ErrorEvent", e -> {
			ErrorEvent ee = (ErrorEvent)e;
			GWT.log("ErrorEvent: "+ee.error.format());
			wToastError(msgMsg.format(ee.error));
		});
		subscribe(".MessageEvent", e -> wToast(((MessageEvent)e).message.format()));
		subscribe(".DeviceChangeEvent", e -> ctx.cDevices.deviceChanged(((DeviceChangeEvent)e).compactView));
		subscribe(".TaskChangeEvent", e -> ctx.cTasks.taskChanged(((TaskChangeEvent)e).compactView));
		subscribe(".TaskScheduledEvent", e -> ctx.cTasks.scheduleChanged(((TaskScheduledEvent)e).scheduleView));
		
		Resources.event.events().doOnTerminate(() -> {/*TODO reconnect*/}).subscribe(e -> demux.accept(e.$type, e));
		
		ctx.cDrivers.drivers().subscribe(); // lets get rolling...
		ctx.cDevices.devices().subscribe(); // lets get rolling...
		ctx.cTasks.tasks().subscribe(); // lets get rolling...
	}
	
	
	@SuppressWarnings("serial")
	private static class MapConsumer<K,E> extends HashMap<K, Consumer<E>> {
		public void accept(K k, E e) { Consumer<E> consumer = get(k); if (consumer != null) consumer.accept(e); }
		public Subscription subscribe(K k, Consumer<E> consumer) { put(k,consumer); return new EventSubscription(() -> remove(k)); }
	}
	
	@SuppressWarnings("serial")
	private static class MapSetConsumer<K,E> extends MapConsumer<K,E> {
		public Subscription subscribe(K k, Consumer<E> consumer) { SetConsumer<E> setConsumer = (SetConsumer<E>) get(k); if (setConsumer == null) put(k,setConsumer = new SetConsumer<>()); return setConsumer.subscribe(consumer); }
	}
	
	@SuppressWarnings("serial")
	private static class SetConsumer<E> extends HashSet<Consumer<E>> implements Consumer<E> {
		public void accept(E e) { forEach(c -> c.accept(e)); }
		public Subscription subscribe(Consumer<E> consumer) { add(consumer); return new EventSubscription(() -> remove(consumer)); }
	}
	
	private static class EventSubscription implements Subscription {
		private Action0 unsubscribe;
		public EventSubscription(Action0 unsubscribe) { this.unsubscribe = unsubscribe; }
		@Override public boolean isUnsubscribed() { return unsubscribe == null; }
		@Override public void unsubscribe() { unsubscribe.call(); unsubscribe = null; }
	}
}
