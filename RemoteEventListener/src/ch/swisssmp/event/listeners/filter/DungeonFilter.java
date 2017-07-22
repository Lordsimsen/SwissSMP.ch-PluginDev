package ch.swisssmp.event.listeners.filter;

import ch.swisssmp.adventuredungeons.event.DungeonEvent;
import ch.swisssmp.adventuredungeons.world.Instancable;
import ch.swisssmp.utils.ConfigurationSection;

public interface DungeonFilter {
	public default boolean checkDungeon(ConfigurationSection dataSection, DungeonEvent event){
		boolean result = true;
		if(dataSection.contains("dungeon_id")){
			result &= dataSection.getInt("dungeon_id")==event.getDungeonId();
		}
		if(dataSection.contains("instance_id") && event instanceof Instancable){
			result &= dataSection.getInt("dungeon_id")==((Instancable)event).getInstanceId();
		}
		return result;
	}
}
