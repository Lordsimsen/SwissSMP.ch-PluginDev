package ch.swisssmp.loginrewards;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener{
	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent event){
		Bukkit.getScheduler().runTaskLater(LoginRewardsPlugin.getInstance(), ()->{
			LoginRewards.trigger(event.getPlayer());
		}, 100L);
	}
}
