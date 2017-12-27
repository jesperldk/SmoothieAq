package jesperl.dk.smoothieaq.server.resources.config;

import javax.ws.rs.core.*;

import org.glassfish.jersey.media.sse.*;

import rx.*;
import rx.exceptions.*;

public class SseRessource {
    protected <T> EventOutput asEventOutput(Class<? extends T> cls, Observable<T> o) {
        final EventOutput eventOutput = new EventOutput();
        final OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
        eventBuilder.mediaType(MediaType.APPLICATION_JSON_TYPE);
        o.subscribe(new Subscriber<T>() {
            @Override public void onStart() { request(1); }
            @Override public void onCompleted() { close(); }
            @Override public void onError(Throwable e) { close(); }
            @Override public void onNext(T t) {
                eventBuilder.data(cls,t); 
                try {
                    eventOutput.write(eventBuilder.build());
                } catch (Throwable e) {
                    close();
                    Exceptions.propagate(e);
                }
                request(1);
            }
            void close() { if (!eventOutput.isClosed()) try { eventOutput.close(); } catch (Throwable t) {}}
        });
        return eventOutput;
    }
}
