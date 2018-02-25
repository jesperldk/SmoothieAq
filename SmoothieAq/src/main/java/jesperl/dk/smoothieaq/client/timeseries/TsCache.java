package jesperl.dk.smoothieaq.client.timeseries;

import static java.lang.Math.*;
import static jesperl.dk.smoothieaq.client.ClientObjects.*;
import static jesperl.dk.smoothieaq.client.timeseries.TsBlock.*;

import java.util.*;
import java.util.logging.*;

import rx.*;
import rx.Observable;
import rx.functions.*;

public class TsCache<E extends TsElement> implements TsSource<E> {
	Logger log = Logger.getLogger(TsCache.class.getName());
	
	protected TsBlock<E> newestBlock;
	protected TsBlock<E> oldestBlock;
	
	final Observable<E> newElements;
	final Func3<Long, Integer, Integer, Observable<E>> readFunc;
	final Func1<Integer, E[]> allocateFunc;
	final Subscription newElementsSubscription;
	
	public TsCache(Observable<E> newElements, Func3<Long, Integer, Integer, Observable<E>> readFunc, Func1<Integer, E[]> allocateFunc) {
		this.newElements = newElements;
		this.readFunc = readFunc;
		this.allocateFunc = allocateFunc;
		setupFirstBlocks();
		read(0l, 0, TsBlock.readSize, null);
		this.newElementsSubscription = readNew(newElements);
	}
	
	protected E[] allocate(int size) { return allocateFunc.call(size); }
	
	protected Subscription readNew(Observable<E> newElements) {
		return newElements.subscribe(new Subscriber<E>() {
			private TsBlock<E> block;
			@Override public void onError(Throwable e) { resouceError(e); }
			@Override public void onNext(E e) {
				if (block == null) {
					block = newestBlock;
					if (newestBlock.olderBlock.oldestP == -1) { 
						newestBlock.oldestIncl = e.stamp; newestBlock.olderBlock.newestNotIncl = e.stamp; 
					} else {
						if (e.stamp < block.oldestIncl) return;
					}
				}
				log.fine(()->"readNew "+d(e.stamp));
				block = block.writeNewer(e);
			}
			@Override public void onCompleted() {}
		});
	}
	
	protected void read(long newestNotIncl, int countNewer, int countOlder, TsBlock<E> blockArg) {
		assert (countNewer > 0) ^ (countOlder > 0);
		log.fine(()->"read "+d(newestNotIncl)+", "+countNewer+", "+countOlder+" - "+blockArg);
		readFunc.call(newestNotIncl, countNewer, countOlder).subscribe(new Subscriber<E>() {
			private boolean readNewer = countNewer > 0;
			private TsBlock<E> block = blockArg;
			private int count = max(countNewer,countOlder);
			@Override public void onError(Throwable e) { resouceError(e); }
			@Override public void onNext(E e) {
				if (block == null) setBlock(e);
				if (e.stamp >= block.newestNotIncl) return;
				count--;
				block = readNewer ? block.writeNewer(e) : block.writeOlder(e);
			}
			private void setBlock(E e) {
				block = oldestBlock;
				if (oldestBlock.newerBlock.newestP == TsBlock.blockSize) { oldestBlock.newestNotIncl = e.stamp+1; oldestBlock.newerBlock.oldestIncl = e.stamp-1; }
			}
			@Override public void onCompleted() {
				if (block == null) return;
				if (countOlder >= 0 && count > 0) block.atEndOfTs = true;
				block.outstandingRequest = false;
				block.updated.onNext(null);
			}
		});
	}
	
	protected void setupFirstBlocks() {
		long now = new Date().getTime();
		newestBlock = new TsBlock<>(this);
		oldestBlock = new TsBlock<>(this);

		newestBlock.atTopOfTs = true;
		newestBlock.newestNotIncl = now;
		newestBlock.newestP = TsBlock.blockSize;
		newestBlock.oldestIncl = now;
		newestBlock.oldestP = TsBlock.blockSize-1;
		newestBlock.olderBlock = oldestBlock;
		newestBlock.olderIsContinous = true;

		oldestBlock.newerBlock = newestBlock;
		oldestBlock.newerIsContinous = true;
		oldestBlock.newestNotIncl = now;
		oldestBlock.newestP = 0;
		oldestBlock.oldestIncl = now;
		oldestBlock.oldestP = -1;
		oldestBlock.outstandingRequest = true;
	}
	
	protected TsBlock<E> findBlock(long stampNotIncl, boolean forReadNewer) {
		log.fine(()->"findBlock "+d(stampNotIncl)+", "+forReadNewer);
		TsBlock<E> block = findBlockInternal(stampNotIncl, forReadNewer);
		log.fine(()->"findBlock found "+block);
		return block;
	}

	private TsBlock<E> findBlockInternal(long stampNotIncl, boolean forReadNewer) {
		TsBlock<E> block = newestBlock;
		if (stampNotIncl == Long.MAX_VALUE) return newestBlock;
		while (block != null && stampNotIncl < block.oldestIncl) block = block.olderBlock;
		if (block == null) return oldestBlock.allocateOlder(stampNotIncl, false);
		if (block.newestNotIncl > stampNotIncl || (block.newestNotIncl == stampNotIncl && !forReadNewer)) return block;
		if (block.newestNotIncl == stampNotIncl && forReadNewer && block.newerIsContinous) return block.newerBlock;
		assert !block.newerIsContinous;
		if (block.newerBlock.oldestIncl == stampNotIncl || forReadNewer) return block.newerBlock.allocateOlder(stampNotIncl, block.newerBlock.oldestIncl == stampNotIncl);
		return block.allocateNewer(stampNotIncl, stampNotIncl == block.newestNotIncl );
	}
	
	public interface Reader<E extends TsElement> { boolean read(E e); } //return true if want more
	private static <E> boolean filter(Func1<E,Boolean> predicate, E e) { return predicate == null || predicate.call(e); }

	@SuppressWarnings("unchecked")
	@Override public Observable<E> elementsFrom(long fromNewestNotIncl, int countNewer, int countOlder, Func1<E,Boolean> predicate) { // returns elements with newest first and then older
		log.fine(()->"elementsFrom "+d(fromNewestNotIncl)+", "+countNewer+", "+countOlder+", "+(predicate != null));
		long frm = (fromNewestNotIncl == 0) ? Long.MAX_VALUE : fromNewestNotIncl;
		return Observable.unsafeCreate(s -> {
			boolean forReadNewer = countNewer > 0;
			TsBlock<E> block = findBlock(frm, forReadNewer);
			int p = block.find(frm);
			Reader<E> reader = new Reader<E>() {
				int newer = countNewer;
				int older = countOlder;
				E[] lookback = (E[]) new TsElement[newer];
				int lookbackp = 0;
				@Override public boolean read(E e) {
					if (s.isUnsubscribed()) return false;
					
					if (newer > lookbackp) { // readingNewer
						if (e == null) { // topOfTs
							log.finest(()->"read newer, missing "+(lookbackp-newer)+" - elementsFrom "+d(fromNewestNotIncl)+", "+countNewer+", "+countOlder+", "+(predicate != null));
							while (newer-- > lookbackp) { if (s.isUnsubscribed()) return false; s.onNext(null); }
						} else if (filter(predicate,e)) {
							lookback[lookbackp++ % countNewer] = e;
							if (newer > lookbackp) return true;
						} else {
							return true;
						}
						// we get here when we have read enough
//						lookbackp = max(0,lookbackp-newer);
						while (newer-- > 0) { if (s.isUnsubscribed()) return false; s.onNext(lookback[(--lookbackp) % countNewer]); }
						if (older > 0) {
							log.finest(()->"done reading newer, now reading older - elementsFrom "+d(fromNewestNotIncl)+", "+countNewer+", "+countOlder+", "+(predicate != null));
							block.readOlder(this, p);
						} else {
							log.finest(()->"done reading newer - elementsFrom "+d(fromNewestNotIncl)+", "+countNewer+", "+countOlder+", "+(predicate != null));
							s.onCompleted();
						}
						return false;
					} else { // readingOlder
						if (e == null) { // endOfTs
							while (older-- > 0) { if (s.isUnsubscribed()) return false; s.onNext(null); }
							log.finest(()->"done reading older, reached eot - elementsFrom "+d(fromNewestNotIncl)+", "+countNewer+", "+countOlder+", "+(predicate != null));
							s.onCompleted();
							return false;
						} else {
							if (s.isUnsubscribed()) return false;
							if (filter(predicate,e)) {
								s.onNext(e);
								if (--older == 0) {
									log.finest(()->"done reading older - elementsFrom "+d(fromNewestNotIncl)+", "+countNewer+", "+countOlder+", "+(predicate != null));
									s.onCompleted();
									return false;
								}
							}
							return true;
						}
					}
				}
				
			};
			if (forReadNewer) block.readNewer(reader, p);
			else block.readOlder(reader, p);
		});
	}
	@Override public Observable<E> newElements(Func1<E,Boolean> predicate) { return newElements; }
	@Override public Observable<Void> refreshListen() { return Observable.never(); }
	@Override public void release() { newElementsSubscription.unsubscribe(); }

}
