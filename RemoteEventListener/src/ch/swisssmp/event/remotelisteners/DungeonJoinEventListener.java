package ch.swisssmp.event.remotelisteners;

import org.bukkit.event.Event;

import ch.swisssmp.adventuredungeons.event.DungeonJoinEvent;
import ch.swisssmp.adventuredungeons.DungeonInstance;
import ch.swisssmp.event.remotelisteners.filter.DungeonFilter;
import ch.swisssmp.event.remotelisteners.filter.PlayerFilter;
import ch.swisssmp.event.remotelisteners.filter.WorldFilter;
import ch.swisssmp.utils.ConfigurationSection;

public class DungeonJoinEventListener extends DungeonEventListener implements DungeonFilter,PlayerFilter,WorldFilter{

	public DungeonJoinEventListener(ConfigurationSection dataSection) {
		super(dataSection);
	}

	@Override
	public void trigger(Event event) {
		if(!(event instanceof DungeonJoinEvent)) return;
		DungeonJoinEvent dungeonJoinEvent = (DungeonJoinEvent) event;
		if(!checkDungeon(this.dataSection, dungeonJoinEvent)) return;
		if(!checkPlayer(this.dataSection, dungeonJoinEvent.getPlayer())) return;
		DungeonInstance dungeonInstance = DungeonInstance.get(dungeonJoinEvent.getInstanceId());
		if(!checkWorld(this.dataSection, dungeonInstance.getWorld())) return;
		super.trigger(event, dungeonJoinEvent.getPlayer());
	}
}
