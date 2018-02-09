package ch.swisssmp.adventuredungeons.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import ch.swisssmp.adventuredungeons.camp.Camp;

public class CampTriggerEvent extends CampEvent implements Cancellable{
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

	public CampTriggerEvent(Camp camp, Player player) {
		super(camp, player);
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

	public static HandlerList getHandlerList(){
		return handlers;
	}
}
