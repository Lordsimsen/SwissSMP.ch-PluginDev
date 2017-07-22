package ch.swisssmp.adventuredungeons.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import ch.swisssmp.adventuredungeons.world.Instancable;

public class DungeonJoinEvent extends DungeonEvent implements Cancellable,Instancable{
    private static final HandlerList handlers = new HandlerList();
    private final int instance_id;
    private final Player player;
    private boolean cancelled = false;
    
	public DungeonJoinEvent(int dungeon_id, Player player, int instance_id) {
		super(dungeon_id);
		this.instance_id = instance_id;
		this.player = player;
	}
	
	public int getInstanceId(){
		return this.instance_id;
	}
	
	public Player getPlayer(){
		return this.player;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		this.cancelled = arg0;
		
	}
	
}
