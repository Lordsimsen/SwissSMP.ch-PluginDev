package ch.swisssmp.weaver;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventListener implements Listener {


    @EventHandler
    private void onBannerPut(InventoryClickEvent event){
        //Todo allow placing of a registered citybanner in the helmet slot
    }

    @EventHandler
    private void onBannerRegister(PlayerInteractEvent event){
        //Todo rightclicking a banner with a sigilring should set it as the city's banner, if possible.

    }
}
