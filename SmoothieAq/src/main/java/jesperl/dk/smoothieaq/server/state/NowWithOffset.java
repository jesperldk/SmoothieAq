package jesperl.dk.smoothieaq.server.state;

import java.time.*;
import java.time.temporal.*;
import java.util.*;
import java.util.concurrent.atomic.*;

public class NowWithOffset {

	private AtomicReference<Clock> clockRef = new AtomicReference<Clock>(Clock.systemDefaultZone());
	private boolean timeIsFlying = false;

	public Clock clock() {
		return clockRef.get();
	}
	
	public Instant instant() {
		return Instant.now(clock());
	}
	
	public LocalDateTime localDateTime() {
		return LocalDateTime.now(clock());
	}
	
	public long millis() {
		return clock().millis();
	}
	
	public Date date() {
		return new Date(millis());
	}
	
	public void setOffset(Duration offsetDuration) {
		clockRef.set(Clock.offset(Clock.systemDefaultZone(), offsetDuration));
	}
	
	public void resetOffset() {
		clockRef.set(Clock.systemDefaultZone());
	}
	
	public void flyToLater(Temporal temporal) {
		Duration offset = Duration.between(Instant.now(), temporal).minus(200, ChronoUnit.MILLIS);
		if (offset.isNegative())
			resetOffset();
		else
			setOffset(offset);
	}
	
	public void flyTo(Temporal temporal) {
		Duration offset = Duration.between(Instant.now(), temporal).minus(200, ChronoUnit.MILLIS);
			setOffset(offset);
	}

	public void setTimeIsFlying(boolean timeIsFlying) {
		this.timeIsFlying = timeIsFlying;
	}
	
	public boolean timeIsFlying() {
		return timeIsFlying;
	}
}
