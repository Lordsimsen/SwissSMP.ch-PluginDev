package ch.swisssmp.transformations;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class EventListener implements Listener{
	@EventHandler(ignoreCancelled=true)
	private void onWorldLoad(WorldLoadEvent event){
		TransformationContainer.load(event.getWorld());
	}
	@EventHandler(ignoreCancelled=true)
	private void onWorldUnload(WorldUnloadEvent event){
		//TransformationContainer.unloadWorld(event.getWorld());
	}
}
