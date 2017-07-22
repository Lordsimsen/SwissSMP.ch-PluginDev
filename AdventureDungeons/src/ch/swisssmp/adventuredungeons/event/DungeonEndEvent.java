package ch.swisssmp.adventuredungeons.event;

import org.bukkit.event.HandlerList;

import ch.swisssmp.adventuredungeons.world.Instancable;

public class DungeonEndEvent extends DungeonEvent implements Instancable{
    private static final HandlerList handlers = new HandlerList();
    private final int instance_id;
    
	public DungeonEndEvent(int dungeon_id, int instance_id) {
		super(dungeon_id);
		this.instance_id = instance_id;
	}
	
	public int getInstanceId(){
		return this.instance_id;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
}
