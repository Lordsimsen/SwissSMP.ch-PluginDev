package ch.swisssmp.camerastudio;

import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

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
    public void onPlayerLeave(final PlayerQuitEvent event) {
        if (plugin.getConfig().getBoolean("clear-points-on-disconnect")
                && CamCommand.points.get(event.getPlayer().getUniqueId()) != null)
            CamCommand.points.get(event.getPlayer().getUniqueId()).clear();
        CameraStudio cameraStudio = CameraStudio.inst();
        if(cameraStudio.isTravelling(event.getPlayer().getUniqueId())){
            cameraStudio.stop(event.getPlayer().getUniqueId());
        }
    }
}
