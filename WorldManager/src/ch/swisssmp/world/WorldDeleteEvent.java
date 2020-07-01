package ch.swisssmp.world;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event when a World is deleted from disk
 * @author detig_iii
 *
 */
public class WorldDeleteEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	
	private final String worldName;
	
    protected WorldDeleteEvent(String worldName) {
		super(true);
		this.worldName = worldName;
	}
    
    public String getWorldName(){
    	return this.worldName;
    }
    
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList(){
		return handlers;
	}
}
