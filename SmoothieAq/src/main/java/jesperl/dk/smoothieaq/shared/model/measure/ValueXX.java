package jesperl.dk.smoothieaq.shared.model.measure;

import com.google.gwt.core.shared.*;

import jsinterop.annotations.*;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ValueXX {
	
	public float value;
	public Unit unit;
	public boolean uncertain;

	@JsOverlay 
	public static ValueXX create(float value, Unit unit) {
		ValueXX v = new ValueXX();
		v.value = value;
		v.unit = unit;
		return v;
	}
	
//	@JsOverlay
//	public final ValueXX inDefaultUnit() {
//		if (unit.getDefaultUnit() == null)
//			return this;
//		else
//			return unit.getDefaultUnit().convertFrom(this);
//	}
//	
//	@JsOverlay
//	public final ValueXX inUnit(Unit inUnit) {
//		return inUnit.convertFrom(this);
//	}

	@Override @GwtIncompatible // TODO @JsOverlay
	public String toString() {
		return unit.toString(value);
	}
	
	@GwtIncompatible // TODO @JsOverlay
	public String toString(int decimals) {
		return unit.toString(value,decimals);
	}

	@GwtIncompatible // TODO @JsOverlay
	public String toStringWithUnit() {
		return unit.toStringWithUnit(value);
	}

	@GwtIncompatible // TODO @JsOverlay
	public String toStringWithUnit(int decimals) {
		return unit.toStringWithUnit(value,decimals);
	}

}
