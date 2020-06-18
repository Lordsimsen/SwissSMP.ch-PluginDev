package ch.swisssmp.camerastudio;

import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;
import ch.swisssmp.utils.PlayerRenameItemEvent;
import ch.swisssmp.utils.SwissSMPler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Lectern;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener {

    private final CameraStudioPlugin plugin;

    protected EventListener(CameraStudioPlugin plugin){
        this.plugin = plugin;
    }

    @EventHandler
    private void onPlayerResourcepackUpdate(PlayerResourcePackUpdateEvent event){
        if(!event.getPlayer().hasPermission("camstudio.admin")) return;
        event.addComponent("camstudio");
    }

    @EventHandler
    private void onWorldLoad(WorldLoadEvent event){
        CameraStudioWorlds.load(event.getWorld());
    }

    @EventHandler
    private void onWorldUnload(WorldUnloadEvent event){
        CameraStudioWorlds.unload(event.getWorld());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        ViewerInfo info = ViewerInfo.load(event.getPlayer()).orElse(null);
        if(info==null) return;
        info.apply(event.getPlayer());
        info.delete();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (plugin.getConfig().getBoolean("clear-points-on-disconnect")
                && CamCommand.points.get(event.getPlayer().getUniqueId()) != null){
            CamCommand.points.get(event.getPlayer().getUniqueId()).clear();
        }
        CameraStudio cameraStudio = CameraStudio.inst();
        // Bukkit.getLogger().info(event.getPlayer().getUniqueId()+" quit");
        if(cameraStudio.isTravelling(event.getPlayer().getUniqueId())){
            // Bukkit.getLogger().info(event.getPlayer().getUniqueId()+" was travelling");
            cameraStudio.abort(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    private void onItemRename(PlayerRenameItemEvent event){
        if(!event.getPlayer().hasPermission("camstudio.admin")) return;
        CameraPathElement element = CameraPathElement.find(event.getItemStack()).orElse(null);
        if(element==null) return;
        element.setName(event.getNewName());
        event.setName(ChatColor.AQUA+element.getName());
        element.getWorld().save();
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event){
        if(event.getItem()==null) return;
        if(event.useInteractedBlock() == Event.Result.ALLOW && event.getClickedBlock().getType().isInteractable() && event.getClickedBlock().getType()!= Material.LECTERN) return;
        if(event.getAction()!= Action.RIGHT_CLICK_BLOCK && event.getAction()!=Action.RIGHT_CLICK_AIR) return;
        ItemStack itemStack = event.getItem();
        CameraPathElement element = CameraPathElement.find(itemStack).orElse(null);
        if(element==null) return;
        if(!event.getPlayer().hasPermission("camstudio.admin")){
            SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.RED+"Keine Berechtigung.");
            return;
        }

        if(element instanceof CameraPathSequence){
            CameraPathSequence sequence = (CameraPathSequence) element;
            if(event.getAction()==Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType()==Material.LECTERN){
                event.setCancelled(true);
                Lectern lectern = (Lectern) event.getClickedBlock().getState();
                if(sequence.getTourBookTemplate()==null){
                    SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.RED+"Zuerst Beschreibungsbuch setzen.");
                    return;
                }

                if(lectern.getInventory().getItem(0)!=null && lectern.getInventory().getItem(0).getType()!=Material.AIR){
                    SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.RED+"Das Lesepult muss leer sein.");
                    return;
                }

                lectern.getInventory().setItem(0, sequence.createTourBook());
                SwissSMPler.get(event.getPlayer()).sendActionBar(ChatColor.GREEN+"Tour-Buch angewendet");
                return;
            }
            CameraPathSequenceEditor.open(event.getPlayer(), sequence);
            return;
        }

        if(element instanceof CameraPath){
            CameraPathEditor.open(event.getPlayer(), (CameraPath) element);
            return;
        }
    }
}
