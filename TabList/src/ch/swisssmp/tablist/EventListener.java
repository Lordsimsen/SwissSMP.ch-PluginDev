package ch.swisssmp.tablist;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {

    @EventHandler(priority= EventPriority.MONITOR)
    private void onPlayerJoin(PlayerJoinEvent event){
        event.setJoinMessage("");
        //setPlayerlistFooter(player, "Livemap: map.swisssmp.ch:8188");
    }
    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        event.setQuitMessage(ChatColor.RESET+"["+ChatColor.RED+"-"+ChatColor.RESET+"] "+player.getDisplayName());
    }
}
