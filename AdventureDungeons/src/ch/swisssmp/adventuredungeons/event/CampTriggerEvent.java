package ch.swisssmp.adventuredungeons.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class CampTriggerEvent extends CampEvent implements Cancellable{
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private boolean cancelled = false;

	public CampTriggerEvent(int dungeon_id, int instance_id, int camp_id, Player player) {
		super(dungeon_id, instance_id, camp_id);
		this.player = player;
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
