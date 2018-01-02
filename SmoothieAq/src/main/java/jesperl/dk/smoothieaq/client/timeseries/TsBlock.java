package jesperl.dk.smoothieaq.client.timeseries;

public class TsBlock<E extends TsElement> {
	private TsCache<E> tsCache;
	
	private long lastAccess;
	private boolean live = false;
	private boolean forward = false; // only relevant if live
	private int lastp = 0;
	
	public static int blockSize = 10000;
	private E[] data;
	private long first;
	private long last;
	
	/*friend*/ TsBlock(TsCache<E> tsCache) {
		this.tsCache = tsCache;
		data = tsCache.allocate(blockSize);
	}
}
