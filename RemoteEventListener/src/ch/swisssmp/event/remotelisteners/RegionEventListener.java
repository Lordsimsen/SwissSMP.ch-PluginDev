package ch.swisssmp.event.remotelisteners;

import org.bukkit.event.Event;

import com.mewin.WGRegionEvents.events.RegionEvent;

import ch.swisssmp.event.remotelisteners.filter.PlayerFilter;
import ch.swisssmp.event.remotelisteners.filter.RegionFilter;
import ch.swisssmp.event.remotelisteners.filter.WorldFilter;
import ch.swisssmp.utils.ConfigurationSection;

public class RegionEventListener extends BasicEventListener implements RegionFilter,PlayerFilter,WorldFilter{

	public RegionEventListener(ConfigurationSection dataSection) {
		super(dataSection);
	}

	@Override
	public void trigger(Event event) {
		if(!(event instanceof RegionEvent)) return;
		RegionEvent regionEvent = (RegionEvent) event;
		if(!checkRegion(this.dataSection, regionEvent)) return;
		if(!checkPlayer(this.dataSection, regionEvent.getPlayer())) return;
		if(!checkWorld(this.dataSection, regionEvent.getPlayer().getWorld())) return;
		super.trigger(event, regionEvent.getPlayer());
	}
	
	@Override
	protected String insertArguments(String command, Event event){
		command = super.insertArguments(command, event);
		RegionEvent regionEvent = (RegionEvent) event;
		command = command.replace("{Region-ID}", String.valueOf(regionEvent.getRegion().getId()));
		command = command.replace("{World}", regionEvent.getPlayer().getWorld().getName());
		return command;
	}
}
