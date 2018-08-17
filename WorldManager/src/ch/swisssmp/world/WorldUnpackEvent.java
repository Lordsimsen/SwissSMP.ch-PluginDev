package ch.swisssmp.world;

import java.io.File;

import org.bukkit.event.HandlerList;

/**
 * Event when a zipped World Folder is unpacked
 * @author detig_iii
 *
 */
public class WorldUnpackEvent extends WorldTransferEvent{
	private static final HandlerList handlers = new HandlerList();
	
    protected WorldUnpackEvent(String worldName, File packedDirectory) {
		super(worldName,packedDirectory);
	}
    
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList(){
		return handlers;
	}
}
