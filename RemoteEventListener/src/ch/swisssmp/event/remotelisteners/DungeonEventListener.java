package ch.swisssmp.event.remotelisteners;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import ch.swisssmp.adventuredungeons.event.DungeonEvent;
import ch.swisssmp.adventuredungeons.Dungeon;
import ch.swisssmp.adventuredungeons.DungeonInstance;
import ch.swisssmp.event.remotelisteners.filter.DungeonFilter;
import ch.swisssmp.event.remotelisteners.filter.WorldFilter;
import ch.swisssmp.utils.ConfigurationSection;

public class DungeonEventListener extends BasicEventListener implements DungeonFilter,WorldFilter{

	public DungeonEventListener(ConfigurationSection dataSection) {
		super(dataSection);
	}

	@Override
	public void trigger(Event event) {
		if(!(event instanceof DungeonEvent)) return;
		DungeonEvent dungeonEndEvent = (DungeonEvent) event;
		if(!checkDungeon(this.dataSection, dungeonEndEvent)) return;
		if(!checkWorld(this.dataSection, dungeonEndEvent.getInstance().getWorld())) return;
		super.trigger(event);
	}
	
	@Override
	protected String insertArguments(String command, Event event){
		command = super.insertArguments(command, event);
		DungeonEvent dungeonEvent = (DungeonEvent) event;
		command = command.replace("{Dungeon-ID}", String.valueOf(dungeonEvent.getDungeonId()));
		if(command.contains("{Dungeon}")){
			Dungeon dungeon = Dungeon.get(dungeonEvent.getDungeonId());
			command = command.replace("{Dungeon}", dungeon.getName());
		}
		command = command.replace("{Instance-ID}", String.valueOf(dungeonEvent.getInstanceId()));
		command = command.replace("{World}", dungeonEvent.getInstance().getWorld().getName());
		return command;
	}
	
	@Override
	protected String insertPlayer(String command, Player player){
		command = super.insertPlayer(command, player);
		if(command.contains("{Instance-ID}")){
			DungeonInstance dungeonInstance = DungeonInstance.get(player);
			if(dungeonInstance!=null){
				command = command.replace("{Instance-ID}", String.valueOf(dungeonInstance.getInstanceId()));
			}
		}
		return command;
	}
	
}
