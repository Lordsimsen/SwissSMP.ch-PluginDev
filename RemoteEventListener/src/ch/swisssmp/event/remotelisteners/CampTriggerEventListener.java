package ch.swisssmp.event.remotelisteners;

import org.bukkit.event.Event;

import ch.swisssmp.adventuredungeons.event.CampTriggerEvent;
import ch.swisssmp.event.remotelisteners.filter.CampFilter;
import ch.swisssmp.event.remotelisteners.filter.DungeonFilter;
import ch.swisssmp.event.remotelisteners.filter.PlayerFilter;
import ch.swisssmp.event.remotelisteners.filter.WorldFilter;
import ch.swisssmp.utils.ConfigurationSection;

public class CampTriggerEventListener extends DungeonEventListener implements CampFilter,PlayerFilter,DungeonFilter,WorldFilter{

	public CampTriggerEventListener(ConfigurationSection dataSection) {
		super(dataSection);
	}

	@Override
	public void trigger(Event event) {
		if(!(event instanceof CampTriggerEvent)) return;
		CampTriggerEvent campTriggerEvent = (CampTriggerEvent) event;
		if(!checkCamp(this.dataSection, campTriggerEvent)) return;
		if(!checkPlayer(this.dataSection, campTriggerEvent.getPlayer())) return;
		if(!checkDungeon(this.dataSection, campTriggerEvent)) return;
		if(!checkWorld(this.dataSection, campTriggerEvent.getInstance().getWorld())) return;
		super.trigger(event, campTriggerEvent.getPlayer());
	}
	
	@Override
	protected String insertArguments(String command, Event event){
		command = super.insertArguments(command, event);
		CampTriggerEvent campTriggerEvent = (CampTriggerEvent) event;
		command = command.replace("{Camp-ID}", String.valueOf(campTriggerEvent.getCampId()));
		return command;
	}
}
