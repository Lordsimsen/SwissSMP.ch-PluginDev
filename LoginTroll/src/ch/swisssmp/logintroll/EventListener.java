package ch.swisssmp.logintroll;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        String replacement = NicknameMap.getReplacement(event.getPlayer().getName());
        if(replacement==null) return;
        event.setQuitMessage(event.getQuitMessage().replace(player.getName(), replacement));
    }
}
