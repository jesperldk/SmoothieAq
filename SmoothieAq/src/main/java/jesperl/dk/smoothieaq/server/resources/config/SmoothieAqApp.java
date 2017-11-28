package jesperl.dk.smoothieaq.server.resources.config;

import java.lang.annotation.*;
import java.util.logging.*;

import javax.ws.rs.*;
//import com.sun.jersey.api.core.DefaultResourceConfig;
//import com.sun.jersey.api.core.ResourceConfig;
//import com.sun.jersey.spi.container.servlet.ServletContainer;
import javax.ws.rs.ext.*;

import org.glassfish.jersey.server.*;

import com.fasterxml.jackson.databind.*;

import jesperl.dk.smoothieaq.server.util.*;

@ApplicationPath("/smoothieaq/x")
public class  SmoothieAqApp extends ResourceConfig {
	private final static Logger log = Logger.getLogger(SmoothieAqApp.class .getName());
	
    public SmoothieAqApp() throws Exception {
    	log.info("**starting "+SmoothieAqApp.class);
    	registerClasses();
    	jesperl.dk.smoothieaq.server.state.State.state().init();
    	
    	doTestStuff();
    }

	private void doTestStuff() {
//		jesperl.dk.smoothieaq.server.state.State state = jesperl.dk.smoothieaq.server.state.State.state();
//		
//		Device device = state.dContext.getAllDrivers().stream().findFirst().get();
//		
//		{
//			Device instance = new Device();
//			instance.dependencyType = DeviceDependencyType.none;
//			instance.name = device.getDescription()+"#1";
//			instance.description = "My very own "+device.getDescription()+", no. 1";
//			instance.deviceType = device.deviceClasse();
//			instance.driverId = device.getId();
//			instance.deviceClass = device.deviceType();
//			state.dContext.addWrapper(WDevice.create(state.dContext, instance));
//		}
//		{
//			Device instance = new Device();
//			instance.dependencyType = DeviceDependencyType.none;
//			instance.name = device.getDescription()+"#1";
//			instance.description = "My other "+device.getDescription()+", for added flow";
//			instance.deviceType = device.deviceClasse();
//			instance.driverId = device.getId();
//			instance.deviceClass = device.deviceType();
//			state.dContext.addWrapper(WDevice.create(state.dContext, instance));
//		}
	}

	public void registerClasses() {System.out.println("**register ");
		FindClass.create("jesperl.dk.smoothieaq.server.resources").filter(c -> isAnnotatedWith(c, Path.class )).forEach(c -> {
			log.info("registering "+c);
			registerClasses(c);
		});
    	registerClasses(ObjectMapperContextResolver.class );
	}

	protected boolean isAnnotatedWith(Class<?> clas, Class<? extends Annotation> annotation) {
		if (clas.getAnnotation(annotation) != null) return true;
		for (Class<?> interf: clas.getInterfaces())
			if (isAnnotatedWith(interf, annotation)) return true;
		return false;
	}

    @Provider
    public static class  ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {
        final ObjectMapper objectMapper = new ObjectMapper();
        {
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            objectMapper.registerModule(new ApplicationJacksonModule());
        }

        @Override public ObjectMapper getContext(Class<?> type) {
            return objectMapper;
        }
    }

}
