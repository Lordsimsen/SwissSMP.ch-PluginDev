package ch.swisssmp.transformations;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class EventListener implements Listener{
	@EventHandler(ignoreCancelled=true)
	private void onWorldLoad(WorldLoadEvent event){
		TransformationWorld.loadWorld(event.getWorld());
	}
	@EventHandler(ignoreCancelled=true)
	private void onWorldUnload(WorldUnloadEvent event){
		TransformationWorld.unloadWorld(event.getWorld());
	}
}
