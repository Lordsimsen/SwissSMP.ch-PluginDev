package ch.swisssmp.event.pluginlisteners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import ch.swisssmp.adventuredungeons.event.DungeonEndEvent;
import ch.swisssmp.adventuredungeons.event.DungeonJoinEvent;
import ch.swisssmp.adventuredungeons.event.DungeonStartEvent;

public class AdventureDungeonsListener implements Listener{
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
}
