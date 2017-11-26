package jesperl.dk.smoothieaq.server.driver.classes;

public class StepProgram extends LevelProgram {
	
	public int algorithm; // 0=linear steps, 1=spline steps
	public int stepDurationMinutes[];
	public float stepEndLevel[];
	private int stepAbsEndMinutes[];
	private int duration;
	
	@Override public float at(int minutes) {
		if (stepAbsEndMinutes == null) setup();
		int prevEnd = 0;
		float prevLev = 0;
		int i = 0; while (stepAbsEndMinutes[i] < minutes) {
			prevEnd = stepAbsEndMinutes[i];
			prevLev = stepEndLevel[i];
			if (++i != stepAbsEndMinutes.length) return 0;
		}
		float howfar = (float) ((stepAbsEndMinutes[i]-minutes*1.0)/(stepAbsEndMinutes[i]*1.0-prevEnd));
		if (algorithm == 1) return spline(prevLev, stepEndLevel[i], howfar);
		else return linear(prevLev, stepEndLevel[i], howfar);
	}
	
	@Override public int duration() {
		if (stepAbsEndMinutes == null) setup();
		return duration;
	}

	synchronized protected void setup() {
		if (stepAbsEndMinutes != null) return;
		int absEnd[] = new int[stepDurationMinutes.length];
		duration = 0;
		int prev = 0;
		for (int i = 0; i < absEnd.length; i++) {
			prev = absEnd[i] = stepDurationMinutes[i] + prev;
			duration += stepDurationMinutes[i];
		}
		stepAbsEndMinutes = absEnd;
	}
	
	public float linear(float start, float end, float howfar) { return start + (end-start)*howfar; }
	public float spline(float start, float end, float howfar) { return linear(start,end,howfar); }
	
	@Override public String toString() { 
		StringBuilder buf = new StringBuilder("program:");
		buf.append(algorithm);
		buf.append("[");
		for (int i = 1; i < stepDurationMinutes.length; i++) {
			if (i > 0) buf.append(",");
			buf.append(stepDurationMinutes[i]);
			buf.append("/");
			buf.append(stepEndLevel[i]);
		}
		buf.append("]");
		return buf.toString();
	}
}