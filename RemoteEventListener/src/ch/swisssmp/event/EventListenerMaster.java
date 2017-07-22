package ch.swisssmp.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.mewin.WGRegionEvents.events.RegionEvent;

import ch.swisssmp.adventuredungeons.event.CampEvent;
import ch.swisssmp.adventuredungeons.event.DungeonEvent;

public class EventListenerMaster implements Listener{
	@EventHandler
	private void CampTriggerEvent(CampEvent event){
		RemoteEventListener.trigger(event);
	}
	@EventHandler
	private void DungeonEvent(DungeonEvent event){
		RemoteEventListener.trigger(event);
	}
	@EventHandler
	private void DungeonEvent(RegionEvent event){
		RemoteEventListener.trigger(event);
	}
}
