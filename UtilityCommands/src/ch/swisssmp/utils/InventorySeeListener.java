package ch.swisssmp.utils;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

public class InventorySeeListener implements Listener {

    @EventHandler
    private void onOpInventoryClick(InventoryClickEvent event){
        Inventory inventory = event.getClickedInventory();
        if(!(inventory instanceof PlayerInventory)) return;
        List<HumanEntity> viewers = inventory.getViewers();
        if(viewers.size() < 2) return;
        Player holder = (Player) ((PlayerInventory) inventory).getHolder();
        Player clicker = (Player) event.getWhoClicked();
        if(holder.isOp()){
            if(clicker != holder) event.setCancelled(true);
        }
    }
}
