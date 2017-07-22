package ch.swisssmp.event.listeners;

import org.bukkit.event.Event;

import ch.swisssmp.adventuredungeons.event.DungeonEvent;
import ch.swisssmp.adventuredungeons.world.Dungeon;
import ch.swisssmp.adventuredungeons.world.Instancable;
import ch.swisssmp.event.listeners.filter.DungeonFilter;
import ch.swisssmp.utils.ConfigurationSection;

public class DungeonEventListener extends DefaultEventListener implements DungeonFilter{

	public DungeonEventListener(ConfigurationSection dataSection) {
		super(dataSection);
	}

	@Override
	public void trigger(Event event) {
		if(!(event instanceof DungeonEvent)) return;
		DungeonEvent dungeonEndEvent = (DungeonEvent) event;
		if(!checkDungeon(this.dataSection, dungeonEndEvent)) return;
		super.trigger(event);
	}
	
	@Override
	protected String insertArguments(String command, Event event){
		DungeonEvent dungeonEvent = (DungeonEvent) event;
		command = command.replace("{Dungeon-ID}", String.valueOf(dungeonEvent.getDungeonId()));
		if(command.contains("{Dungeon}")){
			Dungeon dungeon = Dungeon.get(dungeonEvent.getDungeonId());
			command = command.replace("{Dungeon}", dungeon.name);
		}
		if(dungeonEvent instanceof Instancable){
			command = command.replace("{Instance-ID}", String.valueOf(((Instancable)dungeonEvent).getInstanceId()));
		}
		return command;
	}
	
}
