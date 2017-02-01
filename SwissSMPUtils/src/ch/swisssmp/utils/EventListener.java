package ch.swisssmp.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldSaveEvent;

import ch.swisssmp.webcore.DataSource;

public class EventListener implements Listener{
	protected EventListener(){
		Bukkit.getPluginManager().registerEvents(this, SwissSMPUtils.plugin);
	}
	protected void unregister(){
		HandlerList.unregisterAll(this);
	}
	@EventHandler(ignoreCancelled=true)
	private void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		player.sendMessage(DataSource.getResponse("players/motd.php", new String[]{"player="+player.getUniqueId().toString()}));
		player.performCommand("list");
	}
	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event){
		SwissSMPler.last_vectors.remove(event.getPlayer().getUniqueId());
	}
	@EventHandler(ignoreCancelled=true)
	private void onPlayerChat(AsyncPlayerChatEvent event){
		SwissSMPler player = SwissSMPler.get(event.getPlayer());
		player.setAfk(false);
	}
	@EventHandler(ignoreCancelled=true)
	private void onPlayerInteract(PlayerInteractEvent event){
		SwissSMPler player = SwissSMPler.get(event.getPlayer());
		player.setAfk(false);
	}
	@EventHandler(ignoreCancelled=true)
	private void onWorldSave(WorldSaveEvent event){
		if(event.getWorld()!=Bukkit.getWorlds().get(0)) return;
		SwissSMPler.checkAllAfk(true);
	}
}
