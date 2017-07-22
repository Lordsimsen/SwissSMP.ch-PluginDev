package ch.swisssmp.event.listeners;

import org.bukkit.event.Event;

import com.mewin.WGRegionEvents.events.RegionEvent;

import ch.swisssmp.event.listeners.filter.PlayerFilter;
import ch.swisssmp.event.listeners.filter.TransformationFilter;
import ch.swisssmp.transformations.TransformationEvent;
import ch.swisssmp.utils.ConfigurationSection;

public class TransformationEventListener extends DefaultEventListener implements TransformationFilter, PlayerFilter {

	public TransformationEventListener(ConfigurationSection dataSection) {
		super(dataSection);
	}

	@Override
	public void trigger(Event event) {
		if(!(event instanceof RegionEvent)) return;
		TransformationEvent transformationEvent = (TransformationEvent) event;
		if(!checkTransformation(this.dataSection, transformationEvent)) return;
		if(!checkPlayer(this.dataSection, transformationEvent.getPlayer())) return;
		super.trigger(event, transformationEvent.getPlayer());
	}
}
