package jesperl.dk.smoothieaq.server.access;

import static jesperl.dk.smoothieaq.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.shared.error.Severity.*;
import static jesperl.dk.smoothieaq.shared.util.Objects.*;

import java.util.*;
import java.util.logging.*;
import java.util.stream.*;

import jesperl.dk.smoothieaq.server.access.abstracts.*;
import jesperl.dk.smoothieaq.server.access.classes.*;
import jesperl.dk.smoothieaq.shared.util.*;

public class SaqDeviceAccess extends SaqbDeviceAccess {
	private final static Logger log = Logger.getLogger(SaqDeviceAccess.class.getName());

	public static String bus = bus(SaqDeviceAccess.class);

	public static int emptyCls = 0;
	public static int onoffCls = 1;
	public static int levelCls = 2;
	public static int measureCls = 2;
	public static int bussCls = 10;
	public static int statusCls = 11;

	public static int unspecDev = 0;
	public static int doserDev = 1;
	public static int lightDev = 2;
	public static int fanDev = 3;
	public static int tempDev = 4;
	public static int out5vDev = 5;
	public static int out12vDev = 6;
	public static int measureDev = 7;
	public static int buzzDev = 8;
	public static int timeDev = 9;
	public static int flagDev = 10;
	public static int systemFlagDev = 11;
	public static int systemTempDev = 12;
	public static int plugDev = 13;

	private int logical;

	// URL: saq://smoothieaq-name/logical-dev-no
	@Override protected DeviceUrl getWrapperUrl(DeviceAccessContext context, DeviceUrl daUrl) {
		doGuardedX(() -> {
			logical = Integer.parseInt(daUrl.deviceSelector);
		}, e -> error(log,e,10412, major, "Malformed Saq url: {0}",daUrl.urlString));
		return super.getWrapperUrl(context, daUrl);
	}

	public String[] doLogical(int replyCmd, int cmdNo, String... args) { return doCmd(replyCmd, cmdNo, concat(array(Integer.toString(logical)),args)); }
	
	public void okLogical(int cmdNo, String... args) { doLogical(okReply, cmdNo, args); }
	
	static public Pair<Integer,Integer> dev(String descr) {
		String[] split = descr.split("/");
		return pair(intv(split[0]),intv(split[1]));
	}

	static public List<Pair<DeviceUrl,String>> enumerate(DeviceAccessContext context, int clsId, int typeId) {
		return filter(context.enumerate(SaqDeviceAccess.bus),clsId,typeId).collect(Collectors.toList());
	}
	static public List<String> enumerateUrlString(DeviceAccessContext context, int clsId, int typeId) {
		return filter(context.enumerate(SaqDeviceAccess.bus),clsId,typeId).map(p -> p.a.urlString).collect(Collectors.toList());
	}
	protected static Stream<Pair<DeviceUrl, String>> filter(List<Pair<DeviceUrl, String>> enumerates, int clsId, int typeId) {
		return enumerates.stream()
			.map(p -> pair(p,dev(p.b)))
			.filter(pp -> (clsId < 0 || clsId == pp.b.a) && (typeId < 0 || typeId == pp.b.b))
			.map(pp -> pp.a);
	}
	
	static public List<Pair<DeviceUrl,String>> enumerate(DeviceAccessContext context) {
		List<Pair<DeviceUrl,String>> urls = new ArrayList<>();
		for (Pair<DeviceUrl,String> e: context.enumerate(SaqbDeviceAccess.bus))
			doNoException(() -> {
				SaqbDeviceAccess da = context.get(SaqbDeviceAccess.class,e.a.urlString);
				String[] conf = da.doCmd(confReply, confCmd);
				for (int i = 0; i < conf.length/2; i++) {
					int cmdType = intv(conf[i*2]);
					if (cmdType > 0 && cmdType < 10)
						urls.add(pair(DeviceUrl.create(DeviceUrl.url(SaqDeviceAccess.bus, e.a.busSelector, strv(i))),conf[i*2]+"/"+conf[i*2+1]));
				}
			});
		return urls;
	}

}
