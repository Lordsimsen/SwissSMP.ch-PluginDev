package ch.swisssmp.adventuredungeons.event;

import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.material.MaterialData;

public class MmoBlockChangeEvent extends Event implements Cancellable{
    private static final HandlerList handlers = new HandlerList();
    
    public final Block block;
    public final MaterialData targetData;
    public final UUID player_uuid;
    private boolean cancelled = false;
    
	public MmoBlockChangeEvent(Block block, MaterialData targetData, UUID player_uuid){
		this.block = block;
		this.targetData = targetData;
		this.player_uuid = player_uuid;
	}

	@Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
