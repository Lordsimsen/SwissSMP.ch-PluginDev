package ch.swisssmp.utils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BackLocationListener implements Listener {

    @EventHandler
    private void onTeleport(PlayerTeleportEvent event){
        Player player = event.getPlayer();
        if(!player.hasPermission("smp.commands.back")) return;
        BackCommand.setBackLocation(player, event.getFrom());
    }

    @EventHandler
    private void onDeath(PlayerDeathEvent event){
        Player player = event.getEntity();
        if(!player.hasPermission("smp.commands.back")) return;
        BackCommand.setBackLocation(player, event.getEntity().getLocation());
    }
}
