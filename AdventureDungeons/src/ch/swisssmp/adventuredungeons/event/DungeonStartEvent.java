package ch.swisssmp.adventuredungeons.event;

import org.bukkit.event.HandlerList;

public class DungeonStartEvent extends DungeonEvent{
    private static final HandlerList handlers = new HandlerList();
    
	public DungeonStartEvent(int dungeon_id) {
		super(dungeon_id);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
}
