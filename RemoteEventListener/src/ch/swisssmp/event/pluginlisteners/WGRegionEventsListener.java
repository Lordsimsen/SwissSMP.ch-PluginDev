package ch.swisssmp.event.pluginlisteners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.mewin.WGRegionEvents.events.RegionEnteredEvent;
import com.mewin.WGRegionEvents.events.RegionLeaveEvent;
import com.mewin.WGRegionEvents.events.RegionLeftEvent;

public class WGRegionEventsListener implements Listener{
	@EventHandler
	private void RegionEnterEvent(RegionEnterEvent event){
		EventListenerMaster.getInst().trigger(event);
	}
	@EventHandler
	private void RegionEnteredEvent(RegionEnteredEvent event){
		EventListenerMaster.getInst().trigger(event);
	}
	@EventHandler
	private void RegionLeaveEvent(RegionLeaveEvent event){
		EventListenerMaster.getInst().trigger(event);
	}
	@EventHandler
	private void RegionLeftEvent(RegionLeftEvent event){
		EventListenerMaster.getInst().trigger(event);
	}
}
