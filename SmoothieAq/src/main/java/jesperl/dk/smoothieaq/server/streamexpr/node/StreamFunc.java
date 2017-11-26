package jesperl.dk.smoothieaq.server.streamexpr.node;

public enum StreamFunc {
	not(1),
	and(99),
	or(99),
	xor(2),
	plus(99),
	minus(2),
	times(99),
	division(2),
	div(2,division),
	sum(99,plus),
	above(2),
	below(2),
	equals(2),
	range(3),
	upX(1),
	downX(1),
	Xthen(1),
	forX(2),
	XafterX(2),
	trueZ(0),
	falseZ(0),
	thisZ(1),
	alarmZ(1);
	
	
	private int args;
	private StreamFunc aliasFor = null;
	
	private StreamFunc(int args) { this(args,null); }
	private StreamFunc(int args, StreamFunc aliasFor) { this.args = args; this.aliasFor = aliasFor; }
	
	public int getArgs() { return args; }
	public StreamFunc getAliasFor() { return aliasFor; }
}
