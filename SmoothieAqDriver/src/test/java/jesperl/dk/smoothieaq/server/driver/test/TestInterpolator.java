package jesperl.dk.smoothieaq.server.driver.test;

import static jesperl.dk.smoothieaq.shared.util.Objects.*;

import jesperl.dk.smoothieaq.server.driver.abstracts.*;

public class TestInterpolator {

	public static void main(String[] args) {
		SplineInterpolator si = SplineInterpolator.createMonotoneCubicSpline(list(15.0f,20.0f,25.0f,30.0f,35.0f), list(3.998070105f,4.000628691f,4.004823328f,4.010654014f,4.018120751f));
		SplineInterpolator si4 = SplineInterpolator.createMonotoneCubicSpline(list(15.0f,20.0f,25.0f,30.0f,35.0f), list(ph4NIST(15.0f),ph4NIST(20.0f),ph4NIST(25.0f),ph4NIST(30.0f),ph4NIST(35.0f)));
		for (float t = 15.5f; t < 35.0f; t += 0.5f) {
			System.out.println(t+",\t"+si.interpolate(t)+",\t"+ph4NIST(t)+",\t"+si4.interpolate(t)+",\t"+(ph4NIST(t)-si4.interpolate(t)));
		}
	}

	private static float ph4NIST(float t) { return 1617.3f/k(t) - 9.2852f + 0.033311f*k(t) - 2.3211e-5f*k(t)*k(t); }
	private static float k(float t) { return t + 273.15f; }
}
