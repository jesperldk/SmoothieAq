package jesperl.dk.smoothieaq.shared.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.glassfish.jersey.media.sse.*;

import com.intendia.gwt.autorest.client.*;

import jesperl.dk.smoothieaq.shared.model.event.*;
import rx.*;

@AutoRestGwt @Path("eventListener") @Consumes(MediaType.APPLICATION_JSON)
public interface EventRest {
    @GET @Produces(SseFeature.SERVER_SENT_EVENTS) Observable<Event> events();
}
