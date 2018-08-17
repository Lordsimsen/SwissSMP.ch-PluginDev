package ch.swisssmp.dungeongenerator;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class EventListener implements Listener{
	@EventHandler
	private void onWorldLoad(WorldLoadEvent event){
		GeneratorManager.get(event.getWorld());
	}
	@EventHandler
	private void onWorldUnload(WorldUnloadEvent event){
		GeneratorManager manager = GeneratorManager.get(event.getWorld());
		if(manager!=null) manager.unload();
	}
}
