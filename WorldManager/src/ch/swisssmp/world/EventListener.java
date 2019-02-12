package ch.swisssmp.world;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class EventListener implements Listener{
	/**
	 * Save the World settings when the World is saved
	 * @param event - The Event containing the information about the World that is being saved
	 */
	@EventHandler
	private void onWorldSave(WorldSaveEvent event){
		World world = event.getWorld();
		WorldManager.saveWorldSettings(world);
	}
	
	/**
	 * Load the World settings when the World is loaded
	 * @param event - The Event containing the information about the World that is being loaded
	 */
	@EventHandler
	private void onWorldLoad(WorldLoadEvent event){
		WorldManager.loadWorldSettings(event.getWorld());
	}
	
	/**
	 * Unload the World settings when the World is unloaded
	 * @param event - The Event containing the information about the World that is being loaded
	 */
	@EventHandler
	private void onWorldLoad(WorldUnloadEvent event){
		WorldManager.unloadWorldSettings(event.getWorld());
	}
}
