package ch.swisssmp.world;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event when a zipped World Folder is unpacked
 * @author detig_iii
 *
 */
public class WorldDeleteEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	
	private final String worldName;
	
    protected WorldDeleteEvent(String worldName) {
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
