package ch.swisssmp.adventuredungeons.event;

import org.bukkit.event.HandlerList;

import ch.swisssmp.adventuredungeons.world.DungeonInstance;

public class DungeonStartEvent extends DungeonEvent{
    private static final HandlerList handlers = new HandlerList();
    
	public DungeonStartEvent(DungeonInstance dungeonInstance) {
		super(dungeonInstance);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}
}
