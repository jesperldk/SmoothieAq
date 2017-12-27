package jesperl.dk.smoothieaq.server.resources.impl;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.glassfish.jersey.media.sse.*;

import jesperl.dk.smoothieaq.server.resources.config.*;
import jesperl.dk.smoothieaq.server.resources.implsse.*;
import jesperl.dk.smoothieaq.shared.model.event.*;

@Path("eventListener") @Consumes(MediaType.APPLICATION_JSON)
public class EventRestSse extends SseRessource {
    private EventRestImpl eventRest = new EventRestImpl();
    @GET @Produces(SseFeature.SERVER_SENT_EVENTS) public EventOutput events() { return asEventOutput(Event.class, eventRest.events()); }
}