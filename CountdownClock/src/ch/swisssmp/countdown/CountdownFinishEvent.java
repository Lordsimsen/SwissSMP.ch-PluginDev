package ch.swisssmp.countdown;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CountdownFinishEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    
    private final CountdownClock clock;
    
	public CountdownFinishEvent(CountdownClock clock) {
		this.clock = clock;
	}
	
	public CountdownClock getClock(){
		return this.clock;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}
}
