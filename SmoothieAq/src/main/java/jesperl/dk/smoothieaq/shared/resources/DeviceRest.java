package jesperl.dk.smoothieaq.shared.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.google.gwt.core.shared.*;
import com.intendia.gwt.autorest.client.*;

import jesperl.dk.smoothieaq.server.device.classes.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;
import jesperl.dk.smoothieaq.shared.model.task.*;
import jesperl.dk.smoothieaq.util.shared.error.*;
import jsinterop.annotations.*;
import rx.*;

@AutoRestGwt @Path("device") @Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
public interface DeviceRest {    

	@GET Single<Device> get(@QueryParam("id") int deviceId);   
	@PUT Single<DeviceCompactView> create(Device device);
	@POST Single<DeviceCompactView> update(Device device);
	
	@GET @Path("status") Single<DeviceCompactView> getStatus(@QueryParam("id") int deviceId);
	@GET @Path("statuschanges") Single<LegalStatusChanges> getLegalStatusChanges(@QueryParam("id") int deviceId);
	@PUT @Path("status") Single<DeviceCompactView> statusChange(@QueryParam("id") int deviceId, DeviceStatusChange statusChange);
//	@POST @Path("status") Single<DeviceCompactView> forceOn(@QueryParam("id") int deviceId, boolean on);

	@GET @Path("view") Single<DeviceView> getView(@QueryParam("id") int deviceId);
	
	@GET @Path("devices") Observable<DeviceCompactView> devices();
	
	@GET @Path("drivers") Observable<DriverView> drivers();
	
	@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
	public static class  LegalStatusChanges {
		public DeviceStatusChange[] legalChanges;
	}
	
	@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
	public static class  DeviceView {
		public Device device;
		public DeviceStatusType statusType;
//		public boolean on;
		public Task[] tasks; 
	}
	
	@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
	public class  DeviceCompactView {
		public short deviceId;
		public DeviceType deviceType;
		public DeviceClass deviceClass;
		public MeasurementType measurementType;
		public float repeatabilityLevel;
		public String name;
		public String description;
		public DeviceStatusType statusType;
		public float currentValue;
//		public boolean on;
	}
	
	@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
	public class  DriverView {
		public short driverId;
		public DeviceClass deviceClass;
		public Message name;
		public Message description;
		public String[] defaultUrls;
	}

	@GwtIncompatible public static DeviceCompactView compactView(IDevice idev) {
		DeviceCompactView view = new DeviceCompactView();
		Device device = idev.model().getDevice();
		view.deviceId = device.id;
		view.deviceClass = device.deviceClass;
		view.deviceType = device.deviceType;
		view.description = device.description;
		view.name = device.name;
		view.statusType = idev.model().getStatus().statusType;
		view.currentValue = idev.getValue();
		view.measurementType = idev.model().getDevice().measurementType;
		view.repeatabilityLevel = idev.model().getDevice().repeatabilityLevel;
//		view.on = false;
		return view;
	}

	@GwtIncompatible public static DeviceView view(IDevice idev) {
		DeviceView view = new DeviceView();
		view.device = idev.model().getDevice();
		view.tasks = (Task[])idev.model().getTasks().toArray();
		view.statusType = idev.model().getStatus().statusType;
//		view.on = false;
		return view;
	}
}
