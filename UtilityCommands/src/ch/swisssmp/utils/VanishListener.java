package ch.swisssmp.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.UUID;

public class VanishListener implements Listener {

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(player.hasPermission("smp.commands.vanish")) return;
        HashSet<UUID> vanishedPlayers = VanishCommand.getVanishedPlayers();
        if(vanishedPlayers.isEmpty()) return;
        for(UUID vanished : vanishedPlayers){
            player.hidePlayer(UtilityCommandsPlugin.getInstance(), Bukkit.getPlayer(vanished));
        }
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if(!player.hasPermission("smp.commands.vanish")) return;
        HashSet<UUID> vanishedPlayers = VanishCommand.getVanishedPlayers();
        if(vanishedPlayers.isEmpty()) return;
        if(!vanishedPlayers.contains(player.getUniqueId())) return;
        player.performCommand("vanish");
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event){
        Player player = event.getEntity();
        if(!player.hasPermission("smp.commands.vanish")) return;
        HashSet<UUID> vanishedPlayers = VanishCommand.getVanishedPlayers();
        if(vanishedPlayers.isEmpty()) return;
        if(!vanishedPlayers.contains(player.getUniqueId())) return;
        player.performCommand("vanish");
    }
}
