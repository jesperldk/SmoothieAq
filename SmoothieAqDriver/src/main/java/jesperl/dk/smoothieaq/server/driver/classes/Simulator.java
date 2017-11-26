package jesperl.dk.smoothieaq.server.driver.classes;

import static java.lang.Math.*;

import java.util.function.*;

public class Simulator implements Supplier<Float> {
	public float defaultValue;
	public float variation;
	public float drift;
	public long driftMinutes;
	public float currentDrift;
	public long currentDriftMillis;
	public long currentDriftEnd;
	public Simulator(float defaultValue, float variation, float drift, long driftMinutes) {
		this.defaultValue = defaultValue; this.variation = variation; this.drift = drift; this.driftMinutes = driftMinutes;
		this.currentDriftEnd = System.currentTimeMillis()-1;
	}
	@Override public Float get() {
		long now = System.currentTimeMillis();
		if (currentDriftEnd < now) {
			currentDrift = (float) (drift  * 1 + (random()-0.5));
			currentDriftMillis = (long) (driftMinutes * 60 * 1000 * 1 + ((random()*0.6)-0.3));
			currentDriftEnd = now + currentDriftMillis;
//System.out.println("end: drift:"+currentDrift+" minutes:"+(currentDriftMillis/1000/60));
		}
		double driftFactor = (sin(((currentDriftEnd - now)*1.0 / currentDriftMillis) * 2*PI - PI/2) + 1)/2;
		double thisDrift = currentDrift * driftFactor;
		double variationFactor = (random()-0.5) * 2;
		double thisVariation = variation * variationFactor;
//System.out.println("drift:"+thisDrift+"/"+driftFactor+" variation:"+thisVariation+"/"+variationFactor);
		return (float) (defaultValue + thisDrift + thisVariation);
	}
}