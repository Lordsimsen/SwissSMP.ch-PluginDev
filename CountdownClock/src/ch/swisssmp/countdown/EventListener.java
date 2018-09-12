package ch.swisssmp.countdown;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class EventListener implements Listener {
	@EventHandler(ignoreCancelled=true)
	private void onWorldLoad(WorldLoadEvent event){
		CountdownClock.loadAll(event.getWorld());
	}
	@EventHandler(ignoreCancelled=true)
	private void onWorldUnload(WorldUnloadEvent event){
		CountdownClock.cancelAll(event.getWorld());
	}
}
