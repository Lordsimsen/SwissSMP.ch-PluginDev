package ch.swisssmp.event.listeners;

import org.bukkit.event.Event;

import ch.swisssmp.adventuredungeons.event.CampTriggerEvent;
import ch.swisssmp.event.listeners.filter.CampFilter;
import ch.swisssmp.event.listeners.filter.PlayerFilter;
import ch.swisssmp.utils.ConfigurationSection;

public class CampTriggerEventListener extends DefaultEventListener implements CampFilter,PlayerFilter{

	public CampTriggerEventListener(ConfigurationSection dataSection) {
		super(dataSection);
	}

	@Override
	public void trigger(Event event) {
		if(!(event instanceof CampTriggerEvent)) return;
		CampTriggerEvent campTriggerEvent = (CampTriggerEvent) event;
		if(!checkCamp(this.dataSection, campTriggerEvent)) return;
		if(!checkPlayer(this.dataSection, campTriggerEvent.getPlayer())) return;
		super.trigger(event, campTriggerEvent.getPlayer());
	}
}
