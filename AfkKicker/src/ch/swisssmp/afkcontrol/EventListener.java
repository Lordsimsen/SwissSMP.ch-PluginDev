package ch.swisssmp.afkcontrol;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;

public class EventListener implements Listener {
    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event){
        AfkKicker.inst().trackPlayer(event.getPlayer());
    }
    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event){
        AfkKicker.inst().untrackPlayer(event.getPlayer());
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event){
        AfkKicker.inst().reset(event.getPlayer());
    }
    @EventHandler
    private void onPlayerTeleport(PlayerTeleportEvent event){
        AfkKicker.inst().reset(event.getPlayer());
    }
    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event){
        AfkKicker.inst().reset(event.getPlayer());
    }
    @EventHandler
    private void onPlayerInteract(PlayerDropItemEvent event){
        AfkKicker.inst().reset(event.getPlayer());
    }
    @EventHandler
    private void onPlayerChat(AsyncPlayerChatEvent event){
        AfkKicker.inst().reset(event.getPlayer());
    }
    @EventHandler
    private void onPlayerOpenInventory(InventoryOpenEvent event){
        AfkKicker.inst().reset((Player) event.getPlayer());
    }
    @EventHandler
    private void onPlayerCommand(PlayerCommandPreprocessEvent event){
        if(event.getMessage().startsWith("/afk")) return;
        AfkKicker.inst().reset(event.getPlayer());
    }
    @EventHandler
    private void onPlayerCommand(PlayerRespawnEvent event){
        AfkKicker.inst().reset(event.getPlayer());
    }
}
