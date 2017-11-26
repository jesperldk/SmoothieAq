package jesperl.dk.smoothieaq.server.driver.abstracts;

import java.util.*;
import java.util.function.*;
import java.util.logging.*;

import jesperl.dk.smoothieaq.server.access.classes.*;
import jesperl.dk.smoothieaq.server.driver.classes.*;

public abstract class AbstractToggleDriver<S extends AbstractToggleDriver.Storage, D extends DeviceAccess> extends AbstractSensorDriver<S,D> implements ToggleDriver {
	private final static Logger log = Logger.getLogger(AbstractToggleDriver.class.getName());
	
	public static class Storage extends AbstractSensorDriver.Storage {
		public Map<Object, Consumer<Boolean>> onListeners = new HashMap<>();
	}

	@Override protected Supplier<Float> defaultSimulator() { return new Simulator(-1f, 0f, 2f, 5); }

	@Override public float measure() {
		return isOn() ? 1 : 0;
	}
	@Override protected float measureFromStore(Storage s) { throw new RuntimeException(); }
	@Override protected void measureAndStore(Storage s) { throw new RuntimeException(); } 
	
	@Override
	public boolean isOn() {
		if (isSimulate()) return simulator().get() > 0;
		boolean on = measureOn();
		log.info("Measured "+(on?"on":"off")+" on "+getUrl());
		return on;
	}

	protected abstract boolean measureOn();
	protected abstract Object addListener(Consumer<Boolean> listener);
	protected abstract void removeListener(Object listenerKey, Consumer<Boolean> listener);
	
	@Override public void listen(Consumer<Float> listener) { listenOn(on -> listener.accept(on?1f:0f)); }
	@Override
	public void listenOn(Consumer<Boolean> listener) {
		Consumer<Boolean> logListener = on -> { 
			log.info("Triggered "+(on?"on":"off")+" on "+getUrl()); 
			listener.accept(on); 
		};
		useStorage(s -> s.onListeners.put(addListener(logListener), logListener)); 
	}
	
	@Override public void release() {
		useStorage(s -> {
			for (Map.Entry<Object, Consumer<Boolean>> e: s.onListeners.entrySet()) removeListener(e.getKey(), e.getValue());
			s.onListeners.clear();
		});
		super.release();
	}

}
