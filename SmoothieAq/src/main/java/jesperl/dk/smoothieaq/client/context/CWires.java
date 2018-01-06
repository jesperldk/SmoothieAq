package jesperl.dk.smoothieaq.client.context;

import static jesperl.dk.smoothieaq.client.context.CContext.*;

import java.util.*;
import java.util.function.*;

import com.google.gwt.core.client.*;

import static jesperl.dk.smoothieaq.client.components.GuiUtil.*;

import jesperl.dk.smoothieaq.client.*;
import jesperl.dk.smoothieaq.client.timeseries.*;
import jesperl.dk.smoothieaq.shared.model.event.*;
import rx.*;
import rx.Observer;
import rx.functions.*;

public class CWires {
	
	private MapSetConsumer<String,Event> demux = new MapSetConsumer<>();
	private MapConsumer<Short,TsMeasurement> measurementDemux = new MapConsumer<>();
	
//	public Observable<Event> observable(Class<? extends Event> eventClass) { return subscribe(eventClass, e -> observer.onNext(e)); }
	public Subscription subscribe(Class<? extends Event> eventClass, Observer<Event> observer) { return subscribe(eventClass, e -> observer.onNext(e)); }
	public Subscription subscribe(Class<? extends Event> eventClass, Consumer<Event> consumer) { return demux.subscribe("."+eventClass.getSimpleName(), consumer); }
	
	/*friend*/ Subscription subscribeMeasurement(short deviceId, short streamId, Observer<TsMeasurement> observer) { return measurementDemux.subscribe((short)(deviceId*256+streamId), m -> observer.onNext(m)); }
	
	public void init() {
		Resources.event.events().doOnTerminate(() -> {/*TODO reconnect*/}).subscribe(e -> demux.accept(e.$type, e));
		subscribe(ME.class, e -> { ME me = (ME)e; measurementDemux.accept(me.i, new TsMeasurement(me)); });
		
		subscribe(ErrorEvent.class, e -> {
			ErrorEvent ee = (ErrorEvent)e;
			GWT.log("ErrorEvent: "+ee.error.defaultMessage);
			wToastError(ee.error.format());
		});
		subscribe(MessageEvent.class, e -> wToast(((MessageEvent)e).message.format()));
		subscribe(DeviceChangeEvent.class, e -> ctx.cDevices.deviceChanged(((DeviceChangeEvent)e).compactView));
	}
	
	@SuppressWarnings("serial")
	private static class MapConsumer<K,E> extends HashMap<K, Consumer<E>> {
		public void accept(K k, E e) { Consumer<E> consumer = get(k); if (consumer != null) consumer.accept(e); }
		public Subscription subscribe(K k, Consumer<E> consumer) { put(k,consumer); return new EventSubscription(() -> remove(k)); }
	}
	
	@SuppressWarnings("serial")
	private static class MapSetConsumer<K,E> extends MapConsumer<K,E> {
		public Subscription subscribe(K k, Consumer<E> consumer) { SetConsumer<E> setConsumer = (SetConsumer<E>) get(k); if (setConsumer == null) put(k,setConsumer = new SetConsumer<>()); return setConsumer.subscribe(setConsumer); }
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
