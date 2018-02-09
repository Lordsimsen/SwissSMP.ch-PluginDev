package ch.swisssmp.adventuredungeons.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import ch.swisssmp.adventuredungeons.world.DungeonInstance;

public class DungeonJoinEvent extends DungeonEvent implements Cancellable{
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private boolean cancelled = false;
    
	public DungeonJoinEvent(DungeonInstance dungeonInstance, Player player) {
		super(dungeonInstance);
		this.player = player;
	}
	
	public Player getPlayer(){
		return this.player;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		this.cancelled = arg0;
		
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList(){
		return handlers;
	}
	
}
