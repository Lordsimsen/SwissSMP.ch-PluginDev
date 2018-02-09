package ch.swisssmp.event.remotelisteners;

import org.bukkit.event.Event;

import ch.swisssmp.adventuredungeons.event.CampClearEvent;
import ch.swisssmp.event.remotelisteners.filter.CampFilter;
import ch.swisssmp.event.remotelisteners.filter.DungeonFilter;
import ch.swisssmp.event.remotelisteners.filter.WorldFilter;
import ch.swisssmp.utils.ConfigurationSection;

public class CampClearedEventListener extends DungeonEventListener implements CampFilter,DungeonFilter,WorldFilter{

	public CampClearedEventListener(ConfigurationSection dataSection) {
		super(dataSection);
	}

	@Override
	public void trigger(Event event) {
		if(!(event instanceof CampClearEvent)) return;
		CampClearEvent campClearEvent = (CampClearEvent) event;
		if(!checkCamp(this.dataSection, campClearEvent)) return;
		if(!checkDungeon(this.dataSection, campClearEvent)) return;
		if(!checkWorld(this.dataSection, campClearEvent.getInstance().getWorld())) return;
		super.trigger(event);
	}
	
	@Override
	protected String insertArguments(String command, Event event){
		command = super.insertArguments(command, event);
		CampClearEvent campClearEvent = (CampClearEvent) event;
		command = command.replace("{Camp-ID}", String.valueOf(campClearEvent.getCampId()));
		return command;
	}
}
