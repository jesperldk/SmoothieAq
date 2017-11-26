package jesperl.dk.smoothieaq.server.streamexpr;

import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.util.shared.error.Severity.*;

import java.util.*;

import jesperl.dk.smoothieaq.server.device.*;
import jesperl.dk.smoothieaq.server.streamexpr.node.*;
import jesperl.dk.smoothieaq.util.shared.error.*;
import jesperl.dk.smoothieaq.shared.model.device.*;

public class  StreamExprParser {
	
	public static final String spaceing = " \t";
	public static final String delims = spaceing+",->()";
	public static final String digit = "-0123456789.";
	public static final String arrow = "->";
	public static final String eosToken = "+eos+";
	
	private Map<String, Integer> deviceIds = new HashMap<>();
	private Map<Integer,String> deviceNames = new HashMap<>();
	private String streamExpr;
	private StringTokenizer tokenizer;
	private String thisToken, nextToken;
	
	public StreamApply parseThis(String streamExpr, DeviceContext context) {
		StreamApply sApply = parse(streamExpr, context);
		if (sApply.func != StreamFunc.thisZ) throw error(120103,minor,"Stream does not end in >this<, when parsing >{1}<",streamExpr);
		return sApply;
	}
	
	public StreamApply parse(String streamExpr, DeviceContext context) {
		context.devices().subscribe(d -> {
			Device device = d.model().getDevice();
			deviceIds.put(device.name, (int) device.id);	
			deviceNames.put((int) device.id, device.name);	
		});
		this.streamExpr = streamExpr;
		tokenizer = new StringTokenizer(streamExpr, delims, true);
		forward(); forward();
		StreamApply node = parseApply();
		if (!eos()) throw unexpectedToken();
		return node;
	}

	protected StreamApply parseApply() {
		List<StreamNode> arg = parseArg();
		while (true) {
			StreamApply sApply = parseApply(arg);
			if (!peek().equals(arrow)) return sApply;
			arg = Collections.singletonList(sApply);
		}
	}
	
	protected StreamNode parseNode() {
		if (peek().equals("(") || peekNext().equals(arrow)) return parseApply();
		return parseSimpleNode();
	}

	protected List<StreamNode> parseArg() {
		if (peek().equals("(")) return parseArgList();
		return Collections.singletonList(parseSimpleNode());
	}
	
	protected StreamNode parseSimpleNode() {
		if (digit.contains(peek().substring(0, 1))) return parseConst();
		return parseDevice();
	}

	protected StreamDevice parseDevice() {
		StreamDevice sDevice = new StreamDevice();
		sDevice.token = forward();
		String[] split = sDevice.token.split("\\.");
		if (split.length > 2) throw unknownStream(sDevice.token);
		String[] split2 = split[0].split(":");
		sDevice.deviceName = split2[0];
		if (deviceIds.containsKey(sDevice.deviceName)) {
			sDevice.deviceId = deviceIds.get(sDevice.deviceName);
		} else if (split2.length == 2) {
			doGuardedX(() -> {
				sDevice.deviceId = Integer.parseInt(split2[1]);
				if (!deviceNames.containsKey(sDevice.deviceId)) throw unknownStream(sDevice.token);
				sDevice.deviceName = deviceNames.get(sDevice.deviceId);
			}, e -> unknownStream(sDevice.token));
		} else {
			throw unknownStream(sDevice.token);
		}
		if (split.length > 1) doGuardedX(() -> sDevice.stream = DeviceStream.valueOf(split[1]), e -> unknownStream(sDevice.token));
		return sDevice;
	}

	protected ErrorException unknownStream(String streamStr) {
		return error(120103,minor,"Unknown stream >{0}< parsing >{1}<",streamStr,streamExpr);
	}

	protected StreamApply parseApply(List<StreamNode> preArgs) {
		StreamApply sApply = new StreamApply();
		sApply.args.addAll(preArgs);
		sApply.argsAfter = sApply.args.size();
		expects(arrow);
		sApply.func = parseFunc();
		if (peek().equals("(")) sApply.args.addAll(parseArgList());
		if (sApply.func.getArgs() != 99 && sApply.func.getArgs() != sApply.args.size()) throw error(120102,minor,"Unexpected number of arguments to function >{0}< parsing >{1}<",sApply.func,streamExpr);
		return sApply;
	}

	protected StreamFunc parseFunc() {
		String funcStr = forward();
		StreamFunc func;
		try {
			func = StreamFunc.valueOf(funcStr);
		} catch (Exception e) {
			try {
				func = StreamFunc.valueOf(funcStr+"Z");
			} catch (Exception e2) {
				throw error(120103,minor,"Unknown function >{0}< parsing >{1}<",funcStr,streamExpr);
			}
		}
		if (func.getAliasFor() != null) func = func.getAliasFor();
		return func;
	}

	protected List<StreamNode> parseArgList() {
		List<StreamNode> args = new ArrayList<>();
		expects("(");
		while (true) {
			args.add(parseNode());
			if (!peek().equals(",")) break;
			forward();
		}
		expects(")");
		return args;
	}

	protected StreamConst parseConst() {
		StreamConst sConst = new StreamConst();
		sConst.token = forward();
		doGuarded(() -> sConst.value = Float.parseFloat(sConst.token), e -> error(120101,minor,"Not a floating point value >{0}< parsing >{1}<",sConst.token,streamExpr));
		return sConst;
	}

	protected String peek() { return thisToken; }
	protected String peekNext() { return nextToken; }
	protected boolean eos() { return thisToken == eosToken; }
	protected String forward() {
		String token = thisToken;
		thisToken = nextToken;
		if (nextToken == eosToken) return token;
		while (true) {
			if (!tokenizer.hasMoreTokens()) { nextToken = eosToken; return token; }
			nextToken = tokenizer.nextToken();
			if (nextToken.equals("-") && tokenizer.hasMoreElements()) {
				nextToken += tokenizer.nextToken(); return token;
			}
			if (!spaceing.contains(nextToken)) return token; 
		}
	}
	protected void expects(String expected) { if (!peek().equals(expected)) unexpectedToken(); forward(); }
	protected ErrorException unexpectedToken() {
		return error(120100,minor,"Unexpected token >{0}< parsing >{1}<",peek(),streamExpr);
	}
	
}
