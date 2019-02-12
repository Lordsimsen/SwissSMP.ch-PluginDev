package ch.swisssmp.world.transfer;

import java.io.File;

import org.bukkit.event.HandlerList;

/**
 * Event when a World Folder is packed into a Zip File
 * @author detig_iii
 *
 */
public class WorldPackEvent extends WorldTransferEvent{
    private static final HandlerList handlers = new HandlerList();
    
    protected WorldPackEvent(String worldName, File packedDirectory) {
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
