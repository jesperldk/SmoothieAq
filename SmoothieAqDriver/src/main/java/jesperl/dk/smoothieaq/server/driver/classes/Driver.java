package jesperl.dk.smoothieaq.server.driver.classes;

import java.util.*;

import jesperl.dk.smoothieaq.server.access.abstracts.*;
import jesperl.dk.smoothieaq.server.access.classes.*;
import jesperl.dk.smoothieaq.server.driver.abstracts.AbstractDriver.*;
import jesperl.dk.smoothieaq.shared.error.*;
import jesperl.dk.smoothieaq.shared.util.*;

public interface Driver {

	Message name();
	Message description();
	List<String> getDefaultUrls(DeviceAccessContext context);

	void init(DeviceAccessContext context, String urlString, float[] calibration);

	DeviceUrl getUrl();
	StepInfo[] calibrationInfo();
	int daysBetweenCalibration();
	float[] startCalibration();
	Pair<List<Message>, float[]> calibrateStep(int stepId, float[] stepValues, float[] calibration);
	float[] finalizeCalibration(float[] calibration);

	void release();
}