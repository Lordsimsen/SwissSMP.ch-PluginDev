package ch.swisssmp.adventuredungeons.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import ch.swisssmp.adventuredungeons.camp.Camp;

public class CampClearEvent extends CampEvent{
    private static final HandlerList handlers = new HandlerList();

	public CampClearEvent(Camp camp, Player player) {
		super(camp, player);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}
}
