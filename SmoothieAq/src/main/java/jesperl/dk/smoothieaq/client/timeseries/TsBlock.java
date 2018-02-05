package jesperl.dk.smoothieaq.client.timeseries;

import rx.subjects.*;

public class TsBlock<E extends TsElement> {
	private TsCache<E> tsCache;
	
	public static int blockSize = 50;
	private E[] data;
	
	protected TsBlock<E> newerBlock = null;
	protected boolean newerIsContinous = false;
	protected boolean atTopOfTs = false;
	protected long newestNotIncl = 0;
	protected int newestP = -1;
	
	protected TsBlock<E> olderBlock = null;
	protected boolean olderIsContinous = false;
	protected boolean atEndOfTs = false;
	protected long oldestIncl;
	protected int oldestP = -1;
	
	private final Subject<Void, Void> updated = PublishSubject.create();
	private boolean outstandingRequest = false;
	
	protected TsBlock(TsCache<E> tsCache) {
		this.tsCache = tsCache;
		data = tsCache.allocate(blockSize);
	}
}
