package ch.swisssmp.adventuredungeons.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ch.swisssmp.adventuredungeons.world.Dungeon;
import ch.swisssmp.adventuredungeons.world.DungeonInstance;

public abstract class DungeonEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private final DungeonInstance dungeonInstance;
	
	public DungeonEvent(DungeonInstance dungeonInstance){
		this.dungeonInstance = dungeonInstance;
	}
	
	public int getDungeonId(){
		return this.dungeonInstance.getDungeonId();
	}
	
	public Dungeon getDungeon(){
		return this.dungeonInstance.getDungeon();
	}
	
	public int getInstanceId(){
		return this.dungeonInstance.getInstanceId();
	}
	
	public DungeonInstance getInstance(){
		return this.dungeonInstance;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList(){
		return handlers;
	}
}
