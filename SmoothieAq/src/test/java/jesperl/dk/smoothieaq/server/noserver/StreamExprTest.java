package jesperl.dk.smoothieaq.server.noserver;

import jesperl.dk.smoothieaq.server.*;
import jesperl.dk.smoothieaq.server.device.*;
import jesperl.dk.smoothieaq.server.resources.*;
import jesperl.dk.smoothieaq.server.resources.impl.*;
import jesperl.dk.smoothieaq.server.streamexpr.*;
import jesperl.dk.smoothieaq.server.streamexpr.node.*;
import jesperl.dk.smoothieaq.shared.model.device.*;
import jesperl.dk.smoothieaq.shared.model.measure.*;
import jesperl.dk.smoothieaq.shared.resources.*;

public class  StreamExprTest extends Test {

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
		deviceRest.create(device);
		
		t("1 -> this",state.dContext);
		t("1.2->this",state.dContext);
		t("-1->this",state.dContext);
		t("Temp->this",state.dContext);
		t("Temp->above(2) ->this",state.dContext);
		t("(Temp,Temp->times(2))->sum->this",state.dContext);
		t("(Temp.level,Temp.onoff->times(2))->sum->this",state.dContext);
		t("Tempx->this",state.dContext);
	}
	
	private static void t(String streamExpr, DeviceContext context) {
		System.out.println(streamExpr);
		try {
			StreamNode sNode = new StreamExprParser().parse(streamExpr, context);
			System.out.println("    "+sNode.toString());
			System.out.println("    "+sNode.toSaveable());
			System.out.println("    "+sNode.toShowable());
			String showable = new StreamExprParser().parse(sNode.toSaveable(), context).toShowable();
			String saveable2 = new StreamExprParser().parse(showable,context).toSaveable();
			assert sNode.toSaveable().equals(saveable2);
		} catch (Exception e) { e.printStackTrace(); }
	}

}
