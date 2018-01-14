package jesperl.dk.smoothieaq.server.driver;

import static java.lang.Math.*;
import static jesperl.dk.smoothieaq.server.driver.AD7793AdcUtil.*;
import static jesperl.dk.smoothieaq.util.server.Utils.*;
import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.util.*;

import com.pi4j.io.spi.*;

import jesperl.dk.smoothieaq.server.access.*;
import jesperl.dk.smoothieaq.server.access.abstracts.*;
import jesperl.dk.smoothieaq.server.access.classes.*;
import jesperl.dk.smoothieaq.server.driver.abstracts.*;
import jesperl.dk.smoothieaq.util.shared.error.*;
import jesperl.dk.smoothieaq.util.shared.*;

/**
 * Read about the CN0326 at 
 *   http://www.analog.com/en/design-center/reference-designs/hardware-reference-design/circuits-from-the-lab/cn0326.html
 * This driver is based on reverse engineering of the LabView evaluation software
 *   https://ez.analog.com/servlet/JiveServlet/download/142731-1-24752/CN0326-Evaluation%20Software.zip
 * and by reading
 *   http://www.analog.com/media/en/technical-documentation/data-sheets/AD7792_7793.pdf
 * However full calibrations is not done in this driver. Also note that the calibration in the evaluation software not
 * will be used as it requires all measurements to be taken at the same temp as the calibration. 
 * @author jesper
 *
 */
public abstract class  CN0326Driver extends AbstractPhSensorDriver<CN0326Driver.Storage,ByteDeviceAccess> {
	
	public float resistorValue = 5000.0f;
	public float ohmPerDegree = 3.85f;
	
	public static class  Storage extends AbstractPhSensorDriver.Storage {
		public float extVrefVolt;
		public float tempVolt;
	}

	@Override public void init(DeviceAccessContext context, String urlString, float[] calibration) {
		init(context, urlString, ByteDeviceAccess.class , CN0326Driver.class , () -> new Storage(), calibration);
		useDeviceAccess(da -> init(da));
	}
	@Override public Message description() { return msg(20201,
			"CN0326 high precision acidity (pH) and temperature (°C) sensor. "+
			"Preferable use it with a quality pH probe and a R1000 temperature sensor."); }
	@Override public List<String> getDefaultUrls(DeviceAccessContext context) { return list(DeviceUrl.url(SpiDeviceAccess.bus,"0","0."+1000000+"."+SpiMode.MODE_3.getMode())); }

	@Override protected void initCalibration(float[] calibration) { calibration[rtdOhmIdx()] = 1000; super.initCalibration(calibration); }
	@Override protected int calibrationUse() { return rtdOhmIdx() + 1; }
	public final int rtdOhmIdx() { return super.calibrationUse(); }
	
	@Override protected void measureAndStoreSingle(Storage s) {
		useDeviceAccess(da -> {
			s.extVrefVolt = readExtVrefVolt(da);
			s.tempVolt = readTempVolt(da,s.extVrefVolt);
			s.pHVolt = readpHVolt(da,s.extVrefVolt);
			s.temp = (s.tempVolt/(s.extVrefVolt/resistorValue) - s.calibration[rtdOhmIdx()]) / ohmPerDegree;
			idle(da);
		});
	}
	
	public void init(ByteDeviceAccess da) {
		if (isSimulate()) return;
		reallySleep(300);
		flush(da);
		init7793(da);
		reallySleep(500);
		setRegister2Byte(da,regConf,
				0 << 12 | 	// bipolar
				1 << 7  | 	// internal
				0 << 4  | 	// disabled buffer
				0         	// AIN1(+)-AIN1(-)
		);
		setRegister1Byte(da,regIo,
				1 << 2  | 	// IEXC1 to IOUT2; IEXC2 to IOUT1
				1         	// 10uA
		);
		setRegister2Byte(da,regMode,
							// TODO mode???
				9         	// 16,7Hz - rejection at 80dB 50Hz
		);
	}
	
	public void flush(ByteDeviceAccess da) {
		flush7793(da);
		flush7793(da);
		flush7793(da);
	}
		
	/**
	 * Read the conversion data from a channel.
	 * Note, that the CN0326-Evaluation Software uses continuous conversion, but we uses single conversion and
	 * a sleep to avoid waiting on a pin - simpler, time will tell if it is a good idea. 
	 * @param referenceSelect
	 * @param channel
	 * @return
	 */
	public int readData(ByteDeviceAccess da, int referenceSelect, int channel) {
		setRegister2Byte(da,regConf,
				0 << 12 | 								// bipolar
				(referenceSelect & 0x000001) << 7  | 	// 0=external ref, 1= internal ref
				0 << 4  | 								// disabled buffer
				(channel & 0x00000007)          		// 0=AIN1(+)-AIN1(-), 1= AIN2(+)-AIN2(-), 2=AIN3(+)-AIN3(-), 3=AIN1(-)-AIN1(-), 6=temp sensor
		);
		setRegister2Byte(da,regMode,
				1 << 13	|	// single conversion
				9         	// 16,7Hz - rejection at 80dB 50Hz
		);
		reallySleep(200); // 1 ms to power up, conversion of 1/16,7 s, and a little extra
		return getRegister3Byte(da,regData);
	}
	
	public void idle(ByteDeviceAccess da) {
		setRegister2Byte(da,regMode,
				2 << 13	|	// idle
				9         	// 16,7Hz - rejection at 80dB 50Hz
		);
	}
	
	public float codeToVoltsForBipolar(int data, float vref) { return (float) ((data*1.0f)/(pow(2,23)-1)*vref); }
	
	public float readExtVrefVolt(ByteDeviceAccess da) { return codeToVoltsForBipolar(readData(da,1, 2), 1.17f); }
	public float readTempVolt(ByteDeviceAccess da, float extVref) { return codeToVoltsForBipolar(readData(da,0, 1), extVref); }
	public float readpHVolt(ByteDeviceAccess da, float extVref) { return codeToVoltsForBipolar(readData(da,0, 0), extVref); }

	@Override protected List<String> shiftFields() { return list("temperature"); }
	@Override protected StepInfo[] measureCalibrationInfo() {
		return funcStorage(s ->  concat(
			array(new StepInfo(1, 
				array(new StepInfoField(msg(20205,"Resistance of your sensor (usually 1000 ohm or 100 ohm)",10),s.calibration[rtdOhmIdx()])), 
				msg(20204,
					"The CN0326 board wil give the highest precision with a R1000 temeratrue sensor, but you can use "+
					"other sensors, like the more common R100."
			))),
			super.measureCalibrationInfo()
		));
	}
	@Override protected Pair<List<Message>,float[]> measureCalibrateStep(int stepId, float[] stepValues, float[] calibration) {
		if (stepId == 10) calibration[rtdOhmIdx()] = stepValues[0];
		return null; 
	}
}
