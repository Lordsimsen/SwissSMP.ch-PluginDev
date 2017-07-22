package ch.swisssmp.adventuredungeons.event;

import org.bukkit.event.HandlerList;

public class CampClearEvent extends CampEvent{
    private static final HandlerList handlers = new HandlerList();

	public CampClearEvent(int dungeon_id, int instance_id, int camp_id) {
		super(dungeon_id, instance_id, camp_id);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
