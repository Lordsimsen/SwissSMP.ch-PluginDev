package ch.swisssmp.zones;

import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;
import ch.swisssmp.zones.editor.ZoneEditor;
import ch.swisssmp.zones.editor.ZoneEditorView;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
    private void onPlayerInteract(PlayerInteractEvent event){
        if(event.getItem()==null) return;
        if(event.getAction()!= Action.RIGHT_CLICK_BLOCK && event.getAction()!=Action.RIGHT_CLICK_AIR) return;
        if(!event.getPlayer().isSneaking()) return;
        if(!event.getPlayer().hasPermission("zones.admin")) return;
        ItemStack itemStack = event.getItem();
        Zone zone = Zones.getZone(itemStack).orElse(null);
        if(zone==null) return;
        ZoneEditor.get(event.getPlayer()).ifPresent(ZoneEditor::complete);
        ZoneEditorView.open(event.getPlayer(), zone);
        event.setCancelled(true);
    }
}
