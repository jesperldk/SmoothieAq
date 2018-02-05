package jesperl.dk.smoothieaq.client.timeseries;

import java.util.*;

import rx.Observable;
import rx.functions.*;

public abstract class TsCache<E extends TsElement> {
	
	private LinkedList<TsBlock<E>> blocks = new LinkedList<>();
	
	protected abstract E[] allocate(int size);
	
//	protected TsBlock<E> olderBlock(long newest) {
//		ListIterator<TsBlock<E>> itr = blocks.listIterator();
//		TsBlock<E> prevBlock = null;
//		while (itr.hasNext()) {
//			TsBlock<E> block = itr.next();
//			if (block.newest >= newest) {
//				if (block.oldest <= newest || block.eof) return block;
//				prevBlock = block;
//			} else if (prevBlock.oldest+1 == newest && prevBlock.oldestp < TsBlock.blockSize-15) {
//				return prevBlock;
//			} else {
//				itr.previous();
//				TsBlock<E> newBlock = allocateOlderBlock(newest);
//				itr.add(newBlock);
//				return newBlock;
//			}
//		}
//		TsBlock<E> newBlock = allocateOlderBlock(newest);
//		blocks.add(newBlock);
//		return newBlock;
//	}
//
//	protected TsBlock<E> newerBlock(long newest) {
//		ListIterator<TsBlock<E>> itr = blocks.listIterator(blocks.size());
//		TsBlock<E> nextBlock = null;
//		while (itr.hasPrevious()) {
//			TsBlock<E> block = itr.previous();
//			if (block.oldest <= newest) {
//				if (block.newest >= newest || block.tof) return block;
//				nextBlock = block;
//			} else if (nextBlock.newest+1 == newest && nextBlock.newestp > 14) {
//				return nextBlock;
//			} else {
//				itr.next();
//				TsBlock<E> newBlock = allocateNewerBlock(newest);
//				itr.add(newBlock);
//				return newBlock;
//			}
//		}
//		TsBlock<E> newBlock = allocateNewerBlock(newest);
//		blocks.add(0,newBlock);
//		return newBlock;
//	}

	private TsBlock<E> allocateOlderBlock(long newest) {
		// TODO Auto-generated method stub
		return null;
	}

	private TsBlock<E> allocateNewerBlock(long newest) {
		// TODO Auto-generated method stub
		return null;
	}

	Observable<E> search(long from, int count, Func1<E, Boolean> filter) {
		return null;
	}

}
