package ch.swisssmp.event.listeners;

import org.bukkit.event.Event;

import ch.swisssmp.adventuredungeons.event.DungeonJoinEvent;
import ch.swisssmp.event.listeners.filter.DungeonFilter;
import ch.swisssmp.event.listeners.filter.PlayerFilter;
import ch.swisssmp.utils.ConfigurationSection;

public class DungeonJoinEventListener extends DungeonEventListener implements DungeonFilter,PlayerFilter{

	public DungeonJoinEventListener(ConfigurationSection dataSection) {
		super(dataSection);
	}

	@Override
	public void trigger(Event event) {
		if(!(event instanceof DungeonJoinEvent)) return;
		DungeonJoinEvent dungeonJoinEvent = (DungeonJoinEvent) event;
		if(!checkDungeon(this.dataSection, dungeonJoinEvent)) return;
		if(!checkPlayer(this.dataSection, dungeonJoinEvent.getPlayer())) return;
		super.trigger(event, dungeonJoinEvent.getPlayer());
	}
}
