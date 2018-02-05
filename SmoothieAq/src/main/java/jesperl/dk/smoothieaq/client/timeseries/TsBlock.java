package jesperl.dk.smoothieaq.client.timeseries;

import rx.subjects.*;

public class TsBlock<E extends TsElement> {
	private TsCache<E> tsCache;
	
	public static int blockSize = 10000;
	private E[] data;
	protected long newest;
	protected long oldest;
	protected int newestp = -1;
	protected int oldestp = -1;
	protected boolean tof = false;
	protected boolean eof = false;
	protected boolean tob = false;
	protected boolean eob = false;
	
	public final Subject<Void, Void> updated = PublishSubject.create();
	
	
	/*friend*/ TsBlock(TsCache<E> tsCache) {
		this.tsCache = tsCache;
		data = tsCache.allocate(blockSize);
	}
}
