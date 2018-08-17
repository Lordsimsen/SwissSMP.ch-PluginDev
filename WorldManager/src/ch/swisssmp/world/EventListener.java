package ch.swisssmp.world;

import java.io.File;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;

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
	 * Create settings.yml if it is missing
	 * @param event - The Event containing the information about the World that is being loaded
	 */
	@EventHandler
	private void onWorldLoad(WorldLoadEvent event){
		File settingsFile = new File(event.getWorld().getWorldFolder(),"settings.yml");
		if(!settingsFile.exists()) WorldManager.saveWorldSettings(event.getWorld());
	}
}
