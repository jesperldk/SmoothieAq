package jesperl.dk.smoothieaq.shared.model.device;

import static jesperl.dk.smoothieaq.shared.model.device.DeviceClass.*;
import static jesperl.dk.smoothieaq.shared.model.device.DeviceStream.*;
import static jesperl.dk.smoothieaq.shared.model.device.DeviceStream.level;
import static jesperl.dk.smoothieaq.shared.model.device.DeviceStream.onoff;
import static jesperl.dk.smoothieaq.util.shared.Objects.*;

import java.util.*;

import jesperl.dk.smoothieaq.shared.model.db.*;
import jesperl.dk.smoothieaq.util.shared.*;

public class DeviceUtil {

	public static final Map<DeviceClass, Set<DeviceStream>> toStreams = new HashMap<>();
	public static final Map<DeviceClass, Set<DeviceStream>> toClientStreams = new HashMap<>();
	public static final Map<DeviceClass, Set<DeviceStream>> toSaveStreams = new HashMap<>();
	public static final Map<DeviceClass, DeviceStream> toDefaultStream = new HashMap<>();
	public static final Map<DeviceClass, DeviceStream> toPauseShadowStream = new HashMap<>();
	
	private static int D = 1;
	private static int S = 2;
	private static int C = 4;
	private static int P = 8;
	@SafeVarargs private static void d(DeviceClass dc, Pair<DeviceStream,Integer>... ps) {
		Set<DeviceStream> streams = new HashSet<>();
		Set<DeviceStream> clientStreams = new HashSet<>();
		Set<DeviceStream> saveStreams = new HashSet<>();
		for (Pair<DeviceStream,Integer> p: ps) {
			streams.add(p.a);
			if ((p.b & D) > 0) toDefaultStream.put(dc, p.a);
			if ((p.b & P) > 0) toPauseShadowStream.put(dc, p.a);
			if ((p.b & S) > 0) saveStreams.add(p.a);
			if ((p.b & C) > 0) clientStreams.add(p.a);
		}
		toStreams.put(dc, streams);
		toClientStreams.put(dc, clientStreams);
		toSaveStreams.put(dc, saveStreams);
	}
	static {
		d(sensor,			p(level,D+S+C+P),p(measureX,0));
		d(DeviceClass.onoff,p(startstopX,C),p(onoff,0),p(level,D+S+C+P),p(watt,C),p(capacity,0));
		d(DeviceClass.level,p(startstopX,C),p(onoff,0),p(level,D+S+C+P),p(pctlevel,0),p(pgmX,0),p(watt,C),p(capacity,0));
		d(toggle,			p(onoff,D+S+C+P),p(startstopX,C),p(amountX,C));
		d(doser,			p(amountX,D+S+C),p(startstopX,C),p(onoff,0),p(sofar,C+P));
		d(status,			p(startstopX,C),p(onoff,0),p(level,D+S+C+P));
		d(container,		p(level,D+S+C+P),p(amountX,C),p(capacity,0));
		d(calculated,		p(level,D+S+C+P));
		d(manual,			p(level,D+S+C+P),p(measureX,0));
	}
	
	public static DeviceClass fixup(DeviceClass deviceClass) { return EnumField.fixup(DeviceClass.class, deviceClass); }
	public static short id(DeviceClass deviceClass) { return (short) EnumField.fixup(DeviceClass.class, deviceClass).getId(); }

	public static Map<Short, DeviceClass> fromId = new HashMap<>();
	{ for (DeviceClass deviceClass: DeviceClass.values()) { fromId.put((short) deviceClass.getId(), deviceClass); } }
	public static DeviceClass get(short id) { return fromId.get(id); }
}
