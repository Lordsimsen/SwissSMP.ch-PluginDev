package ch.swisssmp.zones;

import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;
import ch.swisssmp.utils.PlayerRenameItemEvent;
import ch.swisssmp.zones.editor.ZoneEditor;
import ch.swisssmp.zones.editor.ZoneEditorView;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener {

    @EventHandler
    private void onResourcepackUpdate(PlayerResourcePackUpdateEvent event){
        event.addComponent("zones");
    }

    @EventHandler
    private void onWorldLoad(WorldLoadEvent event){
        ZoneContainers.load(event.getWorld());
        Zones.updateTokens();
    }

    @EventHandler
    private void onWorldUnload(WorldUnloadEvent event){
        ZoneContainers.unload(event.getWorld());
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event){
        ZoneEditor.get(event.getPlayer()).ifPresent(ZoneEditor::cancel);
    }

    @EventHandler
    private void onInventoryOpen(InventoryOpenEvent event){
        Zones.updateTokens(event.getInventory());
    }

    @EventHandler
    private void onPlayerRenameItem(PlayerRenameItemEvent event){
        Zone zone = Zone.get(event.getItemStack()).orElse(null);
        if(zone==null) return;
        zone.setName(event.getNewName());
        zone.save();
        event.setName(ChatColor.RESET+event.getNewName());
    }

    /**
     * Öffnet Zonen-Menü oder beendet laufenden Zonen-Editor bei Interaktion mit einem Kartografietisch
     */
    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event){
        if(event.useItemInHand() == Event.Result.DENY) return;
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack itemStack = event.getItem();
        Zone zone = Zones.getZone(itemStack).orElse(null);
        if(zone==null || zone.getType()==MissingZoneType.getInstance()) return;
        if(event.useInteractedBlock() != Event.Result.ALLOW ||
                event.getClickedBlock()==null ||
                event.getClickedBlock().getType()!=Material.CARTOGRAPHY_TABLE){
            return;
        }

        ZoneEditor currentEditor = ZoneEditor.get(event.getPlayer()).orElse(null);
        if(currentEditor!=null){
            currentEditor.complete();
            event.setCancelled(true);
            return;
        }

        ZoneEditor.start(event.getPlayer(), zone);
        // ZoneEditorView.open(event.getPlayer(), zone);
        event.setCancelled(true);
    }
}
