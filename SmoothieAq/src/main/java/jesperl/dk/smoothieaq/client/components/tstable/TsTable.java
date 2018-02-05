package jesperl.dk.smoothieaq.client.components.tstable;

import static java.lang.Math.*;
import static java.util.logging.Level.*;

import java.util.*;
import java.util.function.*;
import java.util.logging.*;

import com.google.gwt.core.client.*;

import gwt.material.design.client.ui.html.*;
import jesperl.dk.smoothieaq.client.timeseries.*;
import rx.*;
import rx.Observable;
import rx.Observer;
import rx.functions.*;
import rx.gwt.schedulers.*;
import rx.subjects.*;

public class TsTable extends Div {
	Logger logger = Logger.getLogger("TsTable");
	
	public static int allocateAbove = 20; //100;
	public static int allocateBelow = 30; //200;
	public static int window = 15;//50;
	public final int size = allocateAbove+allocateBelow;
	public final int pLast = size-1;
	
	public Subject<Boolean, Boolean> atBeginning = PublishSubject.create();
	public Subject<Boolean, Boolean> atEnd = PublishSubject.create();
	
	private TsSource<TsRowData> source;
	private Subscription subscription = null;
	private Subscription listenSubscription = null;
	private Subscription refreshSubScription = null;

	private Queue<Action0> queue = new LinkedList<>();
	public Subject<Void, Void> queueAsync = PublishSubject.create();
	public Subscription queueSubscription;
	
	private long[] stamps = new long[size];
	private int pTop;
	private int pBot;
	private int pCurr;
	private boolean atBeginningFlag = true;
	private boolean atEndFlag = true;

	private boolean queuedFillTop = false;
	private boolean queuedFillBottom = false;

	private boolean loaded = false;
	private long unloadedStamp = 0;
	private int p;
	private int n;

	public TsTable(TsSource<TsRowData> source) {
		super("TsTable");
		this.source = source;
		for (int i = 0; i < size; i++) {
			TsRow row = new TsRow(i);
			if (i < allocateAbove) row.setVisible(false);
			row.empty();
			add(row);
		}
	}
	
	public void refresh() {
		if (!loaded) return;
		teardown();
		setup();
	}
	
	@Override protected void onLoad() {
		super.onLoad();
		loaded = true;
		setup();
	}

	protected void setup() {
		queueSubscription = queueAsync.observeOn(GwtSchedulers.requestIdle()).subscribe(v -> {
			if (!queue.isEmpty()) queue.remove().call();
		});
		refreshSubScription = source.refreshListen().doOnNext(v -> refresh()).subscribe();
		
		atBeginningFlag = unloadedStamp == 0;
		if (unloadedStamp == 0) toTop();
		else { atBeginningFlag = false; toStamp(unloadedStamp); }
		
		listenSubscription = source.newElements(null).subscribe(rd -> {
			if (!atBeginningFlag) return;
			if (atHead()) {
				move(pLast,pCurr,true);
				data(pCurr,rd,true);
				if (pBot < pLast) pBot++;
				if (atEndFlag && pBot == pLast) atEndFlag = false;
				p++;
			} else if (pTop > 0) {
				data(--pTop,rd,false);
				if (pTop == 0) atBeginningFlag = false;
			}
		});
	}
	
	@Override protected void onUnload() {
		super.onUnload();
		teardown();
	}

	protected void teardown() {
		unloadedStamp = atHead() ? 0 : stamps[pCurr];
		reset();
		if (refreshSubScription != null) { refreshSubScription.unsubscribe(); refreshSubScription = null; }
		if (queueSubscription != null) { queueSubscription.unsubscribe(); queueSubscription = null; }
		if (listenSubscription != null) { listenSubscription.unsubscribe(); listenSubscription = null; }
		loaded = false;
	}
	
	public void toTop() {
		reset();
		atBeginning.onNext(atBeginningFlag = true);
		read(source.elementsFrom(0,0,allocateBelow,null),allocateAbove,allocateBelow,null);
	}
	
	public void toStamp(long stamp) {
		reset();
		atBeginning.onNext(atBeginningFlag = false);
		read(source.elementsFrom(stamp,allocateAbove,allocateBelow,null),0,size,null);
	}
	
	public void down() {
		if (atTail()) return;
		if (pBot > pCurr && queue.isEmpty()) {
			visible(pCurr, false);
			pCurr++;
			if (atTail()) atEnd.onNext(true);
			if (!queuedFillBottom && pCurr >= allocateAbove+window) { queuedFillBottom = true; queue(()->fillBottom()); }
		} else if (pBot > pCurr && !queuedFillBottom) {
			queuedFillBottom = true; queue(()->fillBottom(), ()->down());
		} else {
			queue(()->down());
		}
	}
	public void up() {
		if (atHead()) return;
		if (pTop < pCurr && queue.isEmpty()) {
			pCurr--;
			visible(pCurr, true);
			if (atHead()) atBeginning.onNext(true);
			if (!queuedFillTop && pCurr <= allocateAbove-window) { queuedFillTop = true; queue(()->fillTop()); }
		} else if (pTop < pCurr && !queuedFillTop) {
			queuedFillTop = true; queue(()->fillTop(), ()->up());
		} else {
			queue(()->up());
		}
	}
	
	protected void fillBottom() {
		assert subscription == null;
		if (atEndFlag) return;
		if (pCurr >= allocateAbove+window) {
			move(0, size-window, window, false);
			pTop = max(0, pTop-window);
			if (pTop == 0) atBeginningFlag = false;
			pBot -= window;
			pCurr -= window;
			p -= window;
		}
		if (size-pBot >= window) {
			long stamp = stamps[pBot];
			int skip = 1; while (stamps[pBot-skip] == stamp) skip++;
			read(source.elementsFrom(stamps[pBot-skip],0,pLast-pBot+skip,null),pBot-skip+1,pLast-pBot+skip,() -> queuedFillBottom = false);
		} else {
			queuedFillBottom = false; nextAction();
		}
	}
	protected void fillTop() {
		assert subscription == null;
		if (atBeginningFlag) return;
		if (pCurr <= allocateAbove-window) {
			move(size-window, 0, window, false);
			pBot  = max(pLast, pBot+window);
			pTop += window;
			pCurr += window;
			p += window;
		}
		if (pTop >= window) {
			long stamp = stamps[pTop];
			int skip = 1; while (stamps[pTop+skip] == stamp) skip++;
			read(source.elementsFrom(stamps[pTop+skip],pTop+skip+1,0,null),0,pTop+skip+1,() -> queuedFillTop = false);
		} else {
			queuedFillTop = true; nextAction();
		}
	}
	
	protected void queue(Action0... actions) {
		if (queue.size() > 25) {
			GWT.log("dropping action on TsTable");
			return;
		}
		for (int i = 0; i < actions.length; i++) queue.add(actions[i]);
		if (subscription == null) queueAsync.onNext(null);
	}
	
	protected void read(Observable<TsRowData> elements, int pStart, int count, Action0 endAction) {
		p = pStart-1;
		n = 0;
		subscription = elements.subscribe(new TsObserver( rd -> {
			++n; if (n > count) return;
			p++; if (p  > pLast) return;
//			if (p < 0) {
//				subscription.unsubscribe(); subscription = null;
//				return;
//			}
			if (rd == null) {
				empty(p);
				if (p < pCurr) { atBeginningFlag = true; pTop = p+1; }
				else { atEndFlag = true; if (p-1 < pBot) pBot = p-1; }
			} else {
				data(p,rd, p >= pCurr);
				if (p == pLast) atEndFlag = false;
				if (p == 0) atBeginningFlag = false;
				if (p > pBot) pBot = p;
				if (p < pTop) pTop = p;
			}
		},endAction));
		if (subscription.isUnsubscribed()) subscription = null; // yeah, it might already be finished before the assignment above...
	}
	
	protected final boolean atHead() { return atBeginningFlag && pCurr == pTop; }
	protected final boolean atTail() { return atEndFlag && pCurr == pBot; }
	protected final TsRow rget(int p) { return (TsRow)getWidget(p); }
	protected final TsRow rremove(int p) { TsRow row = (TsRow)getWidget(p); remove(p); return row; }
	protected final TsRow rinsert(int p, TsRow row) { insert(row, p); return row; }
	protected final TsRow empty(int p) { TsRow row = rget(p); row.empty(); stamps[p] = 0; return row; }
	protected final TsRow visible(int p, boolean visible) { TsRow row = rget(p); row.setVisible(visible); return row; }
	protected final TsRow data(int p, TsRowData rd, boolean visible) { TsRow row = rget(p); row.data(rd); row.setVisible(visible); stamps[p] = rd.stamp(); return row; }
	protected final TsRow move(int fromp, int top, boolean visible) { move(fromp, top, 1, visible); return rget(top); }
	protected final void move(int fromp, int top, int count, boolean visible) {
		if (fromp > top) for (int i = fromp-1; i >= top; i--) 
			stamps[i+count] = stamps[i];
		else for (int i = fromp+count; i < top+count; i++) 
			stamps[i-count] = stamps[i];
		TsRow[] rows = new TsRow[count];
		for (int i = 0; i < count; i++) 
			rows[i] = rremove(fromp);
		for (int i = 0; i < count; i++) 
			rinsert(top+i, rows[i]).setVisible(visible);
	}
	
	protected void reset() {
		for (int i = allocateAbove; i < allocateAbove+allocateBelow; i++) ((TsRow)getWidget(i)).empty();
		pTop = allocateAbove;
		pBot = allocateAbove-1;
		pCurr = pTop;
		flush();
	}
	
	protected void flush() {
		if (subscription != null) { subscription.unsubscribe(); subscription = null; }
		queue.clear();
		queuedFillTop = false;
		queuedFillBottom = false;
	}

	protected void nextAction() {
		if (!queue.isEmpty()) queueAsync.onNext(null);
	}

	private class TsObserver implements Observer<TsRowData> {
		private Consumer<TsRowData> consumer;
		private Action0 endAction;
		public TsObserver(Consumer<TsRowData> consumer, Action0 endAction) { this.consumer = consumer; this.endAction = endAction; }
		@Override public void onCompleted() { onTerminate(); }
		@Override public void onError(Throwable e) { logger.log(SEVERE, "TsTable onError", e); onTerminate(); } // TODO Auto-generated method stub
		@Override public void onNext(TsRowData t) { consumer.accept(t); }
		protected void onTerminate() {
			if (subscription != null) subscription.unsubscribe();
			subscription = null;
			if (endAction != null) endAction.call();
			nextAction();
		}
	}

}
