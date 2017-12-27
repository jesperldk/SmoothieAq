package jesperl.dk.smoothieaq.server.noserver;

import jesperl.dk.smoothieaq.server.*;
import jesperl.dk.smoothieaq.server.device.classes.*;
import jesperl.dk.smoothieaq.server.resources.*;
import jesperl.dk.smoothieaq.server.resources.impl.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;
import jesperl.dk.smoothieaq.shared.resources.*;

public class  DeviceTest extends Test {

	public static void main(String[] args) throws Exception {
		state.dContext.init();
		state.daContext.setSimulate(true);
		
		System.out.println("go");

		DeviceRest deviceRest = new DeviceRestImpl();
		deviceRest.drivers()
			.subscribe(dv -> println(dv.driverId+": "+dv.name+"/"+dv.description+" ["+String.join(",",dv.defaultUrls)+"]"));
		
		
		Device device = new Device();
		deviceRest.drivers()
			.filter(dv -> dv.driverId == 2).subscribe(dv -> { 
				device.driverId = dv.driverId; 
				device.deviceUrl = dv.defaultUrls[0];
				device.description = dv.name.toString();
				device.deviceClass = dv.deviceClass;
			});
		device.name = "Temp";
		device.deviceType = DeviceType.sensor;
		device.measurementType = MeasurementType.temperature;
		device.repeatabilityLevel = 0.1f;
		deviceRest.create(device).subscribe(dv -> {
			println("new dev: "+dv.deviceId);
			SensorDevice sensor = (SensorDevice)state.dContext.getDevice(dv.deviceId);
			sensor.changeStatus(state, DeviceStatusChange.enable);
			println(sensor.measure());
			sensor.stream().subscribe(f -> println(f));
//			Observable.interval(2, TimeUnit.SECONDS,Schedulers.computation())
//				.doOnNext(l -> println(l))
//				.map(l -> (Void)null).subscribe(sensor.pulse());
		});
		Thread.sleep(10000);
	}

}
