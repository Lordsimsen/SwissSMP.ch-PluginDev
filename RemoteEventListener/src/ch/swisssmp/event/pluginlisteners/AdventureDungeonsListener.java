package ch.swisssmp.event.pluginlisteners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import ch.swisssmp.adventuredungeons.event.CampClearEvent;
import ch.swisssmp.adventuredungeons.event.CampTriggerEvent;
import ch.swisssmp.adventuredungeons.event.DungeonEndEvent;
import ch.swisssmp.adventuredungeons.event.DungeonJoinEvent;
import ch.swisssmp.adventuredungeons.event.DungeonStartEvent;
import ch.swisssmp.adventuredungeons.event.ItemDiscoveredEvent;

public class AdventureDungeonsListener implements Listener{
	@EventHandler
	private void CampTriggerEvent(CampTriggerEvent event){
		EventListenerMaster.getInst().trigger(event);
	}
	@EventHandler
	private void CampClearEvent(CampClearEvent event){
		EventListenerMaster.getInst().trigger(event);
	}
	@EventHandler
	private void DungeonEndEvent(DungeonEndEvent event){
		EventListenerMaster.getInst().trigger(event);
	}
	@EventHandler
	private void DungeonJoinEvent(DungeonJoinEvent event){
		EventListenerMaster.getInst().trigger(event);
	}
	@EventHandler
	private void DungeonStartEvent(DungeonStartEvent event){
		EventListenerMaster.getInst().trigger(event);
	}
	@EventHandler
	private void ItemDiscoveredEvent(ItemDiscoveredEvent event){
		EventListenerMaster.getInst().trigger(event);
	}
}
