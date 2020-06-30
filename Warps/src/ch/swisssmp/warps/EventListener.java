package ch.swisssmp.warps;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class EventListener implements Listener {

    @EventHandler
    private void onWorldUnload(WorldUnloadEvent event){
        WarpPoints.unloadWarps(event.getWorld());
    }

    @EventHandler
    private void onWorldLoad(WorldLoadEvent event){
        WarpPoints.loadWarps(event.getWorld());
    }
}
