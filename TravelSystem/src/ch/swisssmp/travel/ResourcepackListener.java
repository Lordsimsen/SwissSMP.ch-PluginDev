package ch.swisssmp.travel;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;

public class ResourcepackListener implements Listener {

	@EventHandler
	private void onResourcepackUpdate(PlayerResourcePackUpdateEvent event){
		event.addComponent("travelsystem");
	}
}
