package ch.swisssmp.adventuredungeons.event;

import org.bukkit.event.Event;

public abstract class DungeonEvent extends Event{
    private final int dungeon_id;
	
	public DungeonEvent(int dungeon_id){
		this.dungeon_id = dungeon_id;
	}
	
	public int getDungeonId(){
		return this.dungeon_id;
	}
}
