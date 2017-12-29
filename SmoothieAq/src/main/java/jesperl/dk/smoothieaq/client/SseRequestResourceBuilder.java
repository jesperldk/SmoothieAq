package jesperl.dk.smoothieaq.client;

import java.util.logging.*;
import java.util.stream.*;

import com.intendia.gwt.autorest.client.*;

import elemental2.dom.*;
import jsinterop.annotations.*;
import jsinterop.base.*;
import rx.*;
import rx.annotations.*;
import rx.internal.producers.*;
import rx.subscriptions.*;

@Experimental //@SuppressWarnings("GwtInconsistentSerializableClass")
public class SseRequestResourceBuilder extends RequestResourceBuilder {
    private static final Logger log = Logger.getLogger(SseRequestResourceBuilder.class.getName());

    @Override @SuppressWarnings({ "unchecked", "deprecation" }) public <T> T as(Class<? super T> container, Class<?> type) {
        if (Stream.of(produces).anyMatch("text/event-stream"::equals) || uri().toLowerCase().endsWith("listener")) {
            return (T) Observable.<String>create(s -> eventSourceSubscription(s));
        } else {
        	return super.as(container, type);
        }
    }

    private <T> void eventSourceSubscription(Subscriber<T> s) {
        final EventSource source = new EventSource(uri());
        final QueuedProducer<T> producer = new QueuedProducer<>(s);
        try {
            s.add(subscribeEventListener(source, "message", evt -> {
                producer.onNext(parse(Js.<MessageEvent<String>>cast(evt).data));
            }));
            s.add(subscribeEventListener(source, "open", evt -> {
                log.fine("Connection opened: " + uri());
            }));
            s.add(subscribeEventListener(source, "error", evt -> {
                log.log(Level.SEVERE, "Error: " + evt);
                if (source.readyState == source.CLOSED) {
                    producer.onError(new RuntimeException("Event source error"));
                }
            }));
            s.setProducer(producer);
            s.add(Subscriptions.create(() -> {
                // hack because elemental API EventSource.close is missing
                Js.<MessagePort>uncheckedCast(source).close();
            }));
        } catch (Throwable e) {
            log.log(Level.FINE, "Received http error for: " + uri(), e);
            s.onError(new RuntimeException("Event source error", e));
        }
    }

    public static Subscription subscribeEventListener(EventSource source, String type, EventListener fn) {
        source.addEventListener(type, fn);
        return Subscriptions.create(() -> source.removeEventListener(type, fn));
    }

    @JsMethod(namespace = "JSON")
    private static native <T> T parse(String text);
}
