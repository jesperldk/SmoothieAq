package jesperl.dk.smoothieaq.server.access.classes;

import static jesperl.dk.smoothieaq.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.shared.error.Severity.*;
import static jesperl.dk.smoothieaq.shared.util.Objects.*;

import java.util.function.*;
import java.util.logging.*;

public class DeviceUrl {
	private final static Logger log = Logger.getLogger(DeviceUrl.class.getName());

	public String urlString;
	public String bus;
	public String busSelector;
	public String[] busArgs;
	public String deviceSelector;
	public String[] deviceArgs;
	
	public static DeviceUrl create(String urlString) {
		try {
			DeviceUrl url = new DeviceUrl();
			String[] split = urlString.split("/", 4);
			if (split.length < 3 || split[0].charAt(split[0].length()-1) != ':' || split[1].length() != 0) throw new RuntimeException();
			url.bus = split[0].substring(0, split[0].length()-1);
			splitPart(split[2], s -> url.busSelector = s, a -> url.busArgs = a);
			splitPart( split.length == 4 ? split[3] : null, s -> url.deviceSelector = s, a -> url.deviceArgs = a);
			url.rebuildUrlString();
			return url;
		} catch (Exception e) {
			throw error(log,e,10001, major, "Malformed deviceAccess url: {0}",urlString);
		}
	}
	
	public void rebuildUrlString() { urlString = url(bus,busSelector,busArgs,deviceSelector,deviceArgs); }
	
	private static void splitPart(String part, Consumer<String> selector, Consumer<String[]> args) {
		String s = "";
		String[] a = new String[0];
		if (isNotNull(part)) {
			String[] split = part.split(":");
			if (split.length > 2 ) throw new RuntimeException();
			s = split[0];
			if (split.length == 2) a= split[1].split("\\.");
		}
		selector.accept(s); args.accept(a);
	}
	
	public String deviceKey() { return url(bus,busSelector,deviceSelector); }

	public static String url(String bus, String busSelector, String deviceSelector) { return bus+"://"+busSelector+"/"+nnv(deviceSelector,""); }
	public static String url(String bus, String busSelector, String[] busArgs, String deviceSelector, String[] deviceArgs) {
		StringBuffer buf = new StringBuffer(bus).append("://");
		buf.append(busSelector); if (busArgs != null && busArgs.length > 0) buf.append(":").append(String.join(".", busArgs));
		buf.append("/");
		if (deviceSelector != null) {
			buf.append(deviceSelector); if (deviceArgs != null && deviceArgs.length > 0) buf.append(":").append(String.join(".", deviceArgs));
		}
		return buf.toString(); 
	}
	
	@Override public String toString() { return urlString; }
}