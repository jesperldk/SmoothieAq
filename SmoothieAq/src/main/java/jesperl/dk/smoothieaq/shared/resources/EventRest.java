package jesperl.dk.smoothieaq.shared.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.intendia.gwt.autorest.client.*;

@AutoRestGwt @Path("event") @Consumes(MediaType.APPLICATION_JSON)
public interface EventRest {
//    @GET @Produces(SseFeature.SERVER_SENT_EVENTS) Observable<Evnt> listen();
}
