package jesperl.dk.smoothieaq.server.streamexpr.node;

import static jesperl.dk.smoothieaq.server.streamexpr.node.StreamFunc.*;
import static jesperl.dk.smoothieaq.util.shared.error.Errors.*;
import static jesperl.dk.smoothieaq.util.shared.error.Severity.*;

import java.util.*;
import java.util.function.Function;

import jesperl.dk.smoothieaq.server.device.*;
import rx.*;
import rx.Observable;
import rx.Observer;
import rx.functions.*;

public class  StreamApply extends StreamNode {
	public StreamFunc func;
	public List<StreamNode> args = new ArrayList<>();
	public int argsAfter;
	
	public void wire(DeviceContext c, Observer<Float> drain, List<Subscription> s) {
		assert func == thisZ;
		s.add(obeservable(0, c, s).subscribe(drain));
	}

	protected Observable<Float> obeservable(int n, DeviceContext context, List<Subscription> subscriptions) {
		return args.get(n).wire(context,subscriptions);
	}

	@Override
	public Observable<Float> wire(DeviceContext c, List<Subscription> s) {
		switch (func) {
			case below: binaryLogical(c, s, (f,v) -> f < v);
			default: throw error(120120,fatal,"Cannot hadle stream expression function {0}",func);
		}
	}

	protected Observable<Float> binaryLogical(DeviceContext c, List<Subscription> s, Func2<? super Float, ? super Float, Boolean> f) {
		return Observable.combineLatest(obeservable(0,c,s), obeservable(1,c,s), (a,b) -> f.call(a, b) ? 1f : 0f);
	}

	protected Observable<Float> binary(DeviceContext c, List<Subscription> s, Func2<? super Float, ? super Float, ? extends Float> f) {
		return Observable.combineLatest(obeservable(0,c,s), obeservable(1,c,s), f);
	}
	
	@Override public String toString() { return to(args.size(),StreamNode::toString); }
	@Override public String toSaveable() { return to(argsAfter,StreamNode::toSaveable); }
	@Override public String toShowable() { return to(argsAfter,StreamNode::toShowable); }

	public String to(int argsAfter, Function<StreamNode, String> f) {
		StringBuilder buf = new StringBuilder();
		if (argsAfter == 1) {
			buf.append(f.apply(args.get(0)));
		} else if (argsAfter > 1) {
			buf.append("(");
			for (int i = 0; i < argsAfter; i++) {
				buf.append(f.apply(args.get(i)));
				if (i < argsAfter-1) buf.append(",");
			}
			buf.append(")");
		}
		buf.append("->");
		String funcStr = func.name();
		if (funcStr.endsWith("Z")) funcStr = funcStr.substring(0, funcStr.length()-1);
		buf.append(funcStr);
		if (argsAfter < args.size()) {
			buf.append("(");
			for (int i = argsAfter; i < args.size(); i++) {
				buf.append(f.apply(args.get(i)));
				if (i < args.size()-1) buf.append(",");
			}
			buf.append(")");
		}
		return buf.toString(); 
	}
}
