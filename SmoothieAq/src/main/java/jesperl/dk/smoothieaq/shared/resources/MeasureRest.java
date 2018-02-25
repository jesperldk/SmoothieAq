package jesperl.dk.smoothieaq.shared.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.intendia.gwt.autorest.client.*;

import jesperl.dk.smoothieaq.shared.model.event.*;
import rx.*;

@AutoRestGwt @Path("measure") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
public interface MeasureRest {  

	@POST @Path("measuresFrom") Observable<ME> measuresFrom(@QueryParam("fromNewestNotIncl") long fromNewestNotIncl, @QueryParam("countNewer") int countNewer, @QueryParam("countOlder") int countOlder, int[] deviceIds);

}
