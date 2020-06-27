package ch.swisssmp.utils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportListener implements Listener {

    @EventHandler
    private void onTeleport(PlayerTeleportEvent event){
        Player player = event.getPlayer();
        if(!player.hasPermission("smp.commands.back")) return;
        BackCommand.setBackLocation(player, event.getFrom());
    }
}
