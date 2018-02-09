package ch.swisssmp.event.remotelisteners.filter;

import ch.swisssmp.adventuredungeons.event.DungeonEvent;
import ch.swisssmp.utils.ConfigurationSection;

public interface DungeonFilter {
	public default boolean checkDungeon(ConfigurationSection dataSection, DungeonEvent event){
		boolean result = true;
		if(dataSection.contains("dungeon_id")){
			result &= dataSection.getInt("dungeon_id")==event.getDungeonId();
		}
		if(dataSection.contains("instance_id")){
			result &= dataSection.getInt("dungeon_id")==event.getInstanceId();
		}
		return result;
	}
}
