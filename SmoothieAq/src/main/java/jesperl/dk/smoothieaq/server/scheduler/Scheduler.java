package jesperl.dk.smoothieaq.server.scheduler;

import static java.time.temporal.ChronoUnit.*;

import java.time.*;
import java.util.*;
import java.util.logging.*;

import jesperl.dk.smoothieaq.server.task.classes.*;
import jesperl.dk.smoothieaq.shared.model.schedule.*;
import jesperl.dk.smoothieaq.util.shared.*;

public class  Scheduler implements Runnable {
	private final static Logger log = Logger.getLogger(Scheduler.class .getName());
	
	public static class  Scheduled implements Comparable<Scheduled> {
		public final Instant at;
		public final ITask task;
		public final boolean forStart;
		public final Schedule schedule;
		public final Interval intv;
		public Scheduled(Instant at, ITask task, boolean forStart, Schedule schedule, Interval intv) {
			this.at = at;
			this.task = task;
			this.forStart = forStart;
			this.schedule = schedule;
			this.intv = intv;
		}
		@Override
		public int compareTo(Scheduled o) {
			int cmp = at.compareTo(o.at);
			if (cmp == 0)
				cmp = new Integer(task.getId()).compareTo((int) o.task.getId());
			return cmp;
		}
	}
	
	private SchedulerContext context;
	private LinkedList<Scheduled> scheduled = new LinkedList<>();
	
	public Scheduler(SchedulerContext context) {
		this.context = context;
	}
	
	public void addToSchedule(ITask task) {
		if (!task.isEnabled()) {
			log.fine(() -> "task not enabled "+task);
			task.scheduled(context.state(), null);
		} else {
			Pair<? extends Schedule, Interval> next = task.model().getTask().next(context);
			if (next == null) {
				log.info(()->"No next for "+task);
				task.scheduled(context.state(), null);
			} else {
				log.fine(() -> "scheduling task "+task);
				addToSchedule(next.b.start(), task, true, next.a, next.b);
				task.scheduled(context.state(), next.b);
			}
		}
	}

	synchronized private void addToSchedule(Instant at, ITask task, boolean forStart, Schedule schedule, Interval intv) {
		Scheduled sch = new Scheduled(at, task, forStart, schedule, intv);
		ListIterator<Scheduled> itr = scheduled.listIterator();
		while (itr.hasNext()) {
			if (itr.next().compareTo(sch) > 0) {
				itr.previous();
				break;
			}
		}
		itr.add(sch);
		log.info(()->"Schedule "+task+"/"+
				(forStart?(schedule instanceof ScheduleInterval?"start":"do"):"end")+
				" for "+at);
		notify();
	}
	
	public synchronized void clear() {
		log.info("Clearing scheduler");
		scheduled.clear();
		notify();
	}

	@Override public void run() { run(-1); }
	
	public void run(int iterations) {
		while (true) { //(!scheduled.isEmpty()) {
			if (iterations != -1 && iterations-- == 0) return;
			while (scheduled.isEmpty())
				try {
					synchronized (this) {			
						wait(100);
					}
				} catch (InterruptedException e) {}
			
			Scheduled peek = scheduled.peek();
			if (peek == null) continue;
			Instant at = peek.at;
			long until = context.instant().until(at,MILLIS);
			if (until > 0) {
				log.info("waiting "+until+" millis");
				if (context.state().now.timeIsFlying()) {
					context.state().now.flyTo(at);
					until = context.instant().until(at,MILLIS);
				}
				try {
					synchronized (this) {			
						wait(until);
					}
				} catch (InterruptedException e) {}
			}
			log.fine(()->"time is now "+context.instant());
			
			Scheduled next;
			synchronized (this) {
				next = scheduled.pop();
				if (next == null) continue;
				if (context.instant().until(next.at,MILLIS) > 0) {
					scheduled.push(next);
					continue;
				}
			}
			
			if (!next.forStart) { // end of interval
				log.info(()->"! "+next.task+"/off at "+context.instant());
				next.task.end(context.state());
				addToSchedule(next.task);
			} else if (next.schedule instanceof ScheduleInterval) { // start of interval
				log.info(()->"! "+next.task+"/on at "+context.instant());
				next.task.start(context.state());
				addToSchedule(next.intv.end(), next.task, false, next.schedule, next.intv);
			} else { // do it
				log.info(()->"! "+next.task+"/do at "+context.instant());
				next.task.start(context.state());
				addToSchedule(next.task);
			}
		}
	}
}
