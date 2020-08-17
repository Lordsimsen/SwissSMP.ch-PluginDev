package ch.swisssmp.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.UUID;

public class VanishListener implements Listener {

    @EventHandler
    private void onVanishedPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(!player.hasPermission("smp.commands.vanish")) return;
        if(!VanishCommand.getVanishedPlayers().contains(player.getUniqueId())) return;
//        if(event.getClickedBlock().getState() instanceof Chest){
//            event.setCancelled(true);
//            Chest chest = (Chest) event.getClickedBlock().getState();
//            player.openInventory(chest.getBlockInventory());
//            SwissSMPler.get(player).sendActionBar(ChatColor.AQUA + " Öffne Kiste leise.");
//            return;
//        } else if(event.getClickedBlock().getState() instanceof ShulkerBox){
//            event.setCancelled(true);
//            ShulkerBox box = (ShulkerBox) event.getClickedBlock().getState();
//            player.openInventory(box.getInventory());
//            SwissSMPler.get(player).sendActionBar(ChatColor.AQUA + " Öffne Kiste leise.");
//            return;
//        }
        SwissSMPler.get(player).sendActionBar(ChatColor.AQUA + " Das kannst du nicht tun wenn du vanished bist.");
        event.setCancelled(true);
    }

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
