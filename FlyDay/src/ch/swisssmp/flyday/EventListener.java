package ch.swisssmp.flyday;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener{
	@EventHandler(ignoreCancelled=true)
	private void onPlayerJoin(PlayerJoinEvent event){
		Bukkit.getScheduler().runTaskLater(FlyDay.plugin, new Runnable(){
			public void run(){
				FlyDay.updatePlayer(event.getPlayer());
			}
		}, 2L);
	}
	
	@EventHandler(ignoreCancelled=true)
	private void onPlayerChangedWorld(PlayerChangedWorldEvent event){
		Bukkit.getScheduler().runTaskLater(FlyDay.plugin, new Runnable(){
			public void run(){
				FlyDay.updatePlayer(event.getPlayer(), false);
			}
		}, 5L);
	}
}
