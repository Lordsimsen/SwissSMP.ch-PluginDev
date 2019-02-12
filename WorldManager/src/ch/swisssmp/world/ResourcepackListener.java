package ch.swisssmp.world;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;

public class ResourcepackListener implements Listener {

	@EventHandler
	private void onResourcepackUpdate(PlayerResourcePackUpdateEvent event){
		if(!event.getPlayer().hasPermission("worldmanager.admin")) return;
		event.addComponent("world_management_tools");
	}
}
