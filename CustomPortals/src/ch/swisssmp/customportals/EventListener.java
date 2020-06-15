package ch.swisssmp.customportals;

import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;
import ch.swisssmp.utils.PlayerRenameItemEvent;
import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener {

    @EventHandler
    private void onPlayerResourcepackUpdate(PlayerResourcePackUpdateEvent event){
        event.addComponent("portals");
    }

    @EventHandler(ignoreCancelled=true)
    private void onRegionEnter(RegionEnterEvent event){
        String regionId = event.getRegion().getId();
        World world = event.getPlayer().getWorld();
        CustomPortal portal = CustomPortal.get(world, regionId).orElse(null);
        if(portal==null) return;
        if(!portal.canTravel(event.getPlayer())) return;
        portal.travel(event.getPlayer());
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event){
        CustomPortalContainers.updateTokens(event.getPlayer());
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event){
        if(event.getItem()==null) return;
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType().isInteractable() && !event.getPlayer().isSneaking()) return;
        Player player = event.getPlayer();
        if(!player.hasPermission("portals.admin")) return;
        ItemStack itemStack = event.getItem();
        CustomPortal portal = CustomPortal.get(itemStack).orElse(null);
        if(portal==null) return;
        CustomPortalEditorView.open(event.getPlayer(), portal);
    }

    @EventHandler
    private void onPlayerItemRename(PlayerRenameItemEvent event){
        Player player = event.getPlayer();
        if(!player.hasPermission("portals.admin")) return;
        ItemStack itemStack = event.getItemStack();
        CustomPortal portal = CustomPortal.get(itemStack).orElse(null);
        if(portal==null) return;
        portal.setName(event.getNewName());
        event.setName(ChatColor.LIGHT_PURPLE+event.getNewName());
        portal.getContainer().save();
        portal.updateTokens();
    }

    @EventHandler
    private void onWorldLoad(WorldLoadEvent event){
        CustomPortalContainers.load(event.getWorld());
        CustomPortalContainers.updateTokens();
    }

    @EventHandler
    private void onWorldUnload(WorldUnloadEvent event){
        CustomPortalContainers.load(event.getWorld());
    }
}
