package ch.swisssmp.davinfinitybucket;

import ch.swisssmp.utils.SwissSMPler;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import javax.sound.sampled.Line;

public class EventListener implements Listener {

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        SwissSMPler.get(player).sendMessage("Hallo " + player.getName() + "!");
    }

    @EventHandler
    private void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event) {

        Player player = event.getPlayer();
        ItemStack itemstack = event.getItemStack();

        Bukkit.getLogger().info("Bis zum If");
        if (!InfinityBucket.isInfinityBucket(itemstack)) return;

        SwissSMPler.get(player).sendMessage("Das Wasserplazierevent wurde abgebrochen!");
    }
}
