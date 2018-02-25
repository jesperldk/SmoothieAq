package jesperl.dk.smoothieaq.client.timeseries;

import java.util.*;
import java.util.logging.*;

import com.google.gwt.core.client.*;

import rx.functions.*;
import rx.subjects.*;

public class TsBlock<E extends TsElement> {
	Logger log = Logger.getLogger(TsBlock.class.getName());

	public static int blockSize = 30;
	public static final int readSize = TsBlock.blockSize-2;
	public static long count = 0;

	private TsCache<E> tsCache;
	private E[] data;
	private long n = ++count;
	
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
	
	protected final Subject<Void, Void> updated = PublishSubject.create();
	protected boolean outstandingRequest = false;
	
	protected TsBlock(TsCache<E> tsCache) {
		this.tsCache = tsCache;
		data = tsCache.allocate(blockSize);
	}
	
	@Override public String toString() { return "TsBlock#"+n+" "+d(newestNotIncl)+" - "+d(oldestIncl); }
	@SuppressWarnings("deprecation") public static String d(long stamp) { return new Date(stamp).toLocaleString(); }

	private void wait(Action0 a) { 
		log.finest(() -> "waiting on "+this);
		updated.first().subscribe(v -> {
			log.finest(() -> "updated on "+this);
			a.call();	
		}); 
	}
	
	protected int find(long stampNotIncl) {
		if (newestNotIncl == stampNotIncl) return newestP-1;
		int p = newestP;
		while (p < oldestP && data[p].stamp >= stampNotIncl) {GWT.log("p="+p+" p.stamp="+d(data[p].stamp)+" stampNotIncl="+d(stampNotIncl)); p++;}
		int foundP = p-1;
		log.finest(() -> "found "+d(stampNotIncl)+" at "+foundP+" - "+this);
		return foundP;
	}

	protected void readNewer(TsCache.Reader<E> reader, int p) {
		int pStart = p;
		log.fine(() -> "readNewer at "+pStart+" - "+this);
		while (true) {
			if (p < newestP) {
				if (atTopOfTs) {
					reader.read(null);
				} else if (newerIsContinous) {
					newerBlock.readNewer(reader, newerBlock.oldestP); // hmm, recursive...
				} else {
					int pp = p;
					wait(()-> readNewer(reader,pp));
					if (!outstandingRequest) tsCache.read(oldestIncl, readSize, 0, this);
				}
				return;
			}
			GWT.log("read newer "+d(data[p].stamp)+" from "+p+" in "+this);
			if (!reader.read(data[p--])) return;
		}
	}
	
	protected void readOlder(TsCache.Reader<E> reader, int p) {
		int pStart = p;
		log.fine(() -> "readOlder at "+pStart+" - "+this);
		while (true) {
			if (p >= oldestP) {
				if (atEndOfTs) {
					reader.read(null);
				} else if (olderIsContinous) {
					olderBlock.readOlder(reader, olderBlock.newestP-1); // hmm, recursive...
				} else {
					int pp = p;
					wait(()-> readOlder(reader,pp));
					if (!outstandingRequest) tsCache.read(oldestIncl, 0, readSize, this);
				}
				return;
			}
			if (!reader.read(data[++p])) return;
		}
	}
	
	protected TsBlock<E> writeNewer(E e) {
		log.finest(() -> "writeNewer "+d(e.stamp)+" - "+this);
		if (newerIsContinous) return this;
		if (newerBlock != null && newerBlock.oldestIncl <= e.stamp) { 
			newerIsContinous = true; newerBlock.olderIsContinous = true; 
			return this; 
		}
		if (newestP == 0) {
			if (newerBlock != null) return newerBlock.writeNewer(e);
			TsBlock<E> block = allocateNewer(e.stamp,true);
			if (!block.atTopOfTs) {
				block.outstandingRequest = true;
				outstandingRequest = false;
				updated.onNext(null);
			}
			return block.writeNewer(e);
		}
		data[--newestP] = e; newestNotIncl = e.stamp+1;
		log.finest(() -> "writeNewer "+d(e.stamp)+" wrote at "+newestP+" - "+this);
		return this;
	}
	
	protected TsBlock<E> allocateNewer(long newStamp, boolean continous) {
		TsBlock<E> block = new TsBlock<>(tsCache);
		if (newerBlock == null) tsCache.newestBlock = block; else newerBlock.olderBlock = block;
		block.newerBlock = newerBlock;
		newerBlock = block;
		block.atTopOfTs = atTopOfTs; atTopOfTs = false;
		block.newestNotIncl = newStamp+1;
		block.newestP = TsBlock.blockSize;
		block.oldestIncl = newStamp;
		block.oldestP = TsBlock.blockSize-1;
		block.olderBlock = this;
		if (continous) {block.olderIsContinous = true; newerIsContinous = true;}
		log.fine(() -> "allocateNewer allocated "+block+" continuos="+newerIsContinous+" - "+this);
		return block;
	}
	
	protected TsBlock<E> writeOlder(E e) {
		log.finest(() -> "writeOlder "+d(e.stamp)+" - "+this);
		assert !atEndOfTs;
		if (olderIsContinous) return this;
		if (olderBlock != null && olderBlock.newestNotIncl > e.stamp) { 
			olderIsContinous = true; olderBlock.newerIsContinous = true; 
			return this; 
		}
		if (oldestP == blockSize-1) {
			if (olderBlock != null) return olderBlock.writeOlder(e);
			TsBlock<E> block = allocateOlder(e.stamp,true);
			block.outstandingRequest = true;
			outstandingRequest = false;
			updated.onNext(null);
			return block.writeOlder(e);
		}
		data[++oldestP] = e; oldestIncl = e.stamp;
		log.finest(() -> "writeOlder "+d(e.stamp)+" wrote at "+oldestP+" - "+this);
		return this;
	}
	
	protected TsBlock<E> allocateOlder(long newStamp, boolean continous) {
		TsBlock<E> block = new TsBlock<>(tsCache);
		if (olderBlock == null) tsCache.oldestBlock = block; else olderBlock.newerBlock = block;
		block.olderBlock = olderBlock;
		olderBlock = block;
		block.oldestIncl = newStamp;
		block.oldestP = -1;
		block.newerBlock = this;
		block.newestNotIncl = continous ? oldestIncl : newStamp+1;
		block.newestP = 0;
		if (continous) {block.newerIsContinous = true; olderIsContinous = true;}
		log.fine(() -> "allocateOlder allocated "+block+" continuos="+olderIsContinous+" - "+this);
		return block;
	}
}
