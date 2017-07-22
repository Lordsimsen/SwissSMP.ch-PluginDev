package ch.swisssmp.event.listeners;

import org.bukkit.event.Event;

import com.mewin.WGRegionEvents.events.RegionEvent;

import ch.swisssmp.event.listeners.filter.PlayerFilter;
import ch.swisssmp.event.listeners.filter.RegionFilter;
import ch.swisssmp.utils.ConfigurationSection;

public class RegionEventListener extends DefaultEventListener implements RegionFilter,PlayerFilter{

	public RegionEventListener(ConfigurationSection dataSection) {
		super(dataSection);
	}

	@Override
	public void trigger(Event event) {
		if(!(event instanceof RegionEvent)) return;
		RegionEvent regionEvent = (RegionEvent) event;
		if(!checkRegion(this.dataSection, regionEvent)) return;
		if(!checkPlayer(this.dataSection, regionEvent.getPlayer())) return;
		super.trigger(event, regionEvent.getPlayer());
	}
}
