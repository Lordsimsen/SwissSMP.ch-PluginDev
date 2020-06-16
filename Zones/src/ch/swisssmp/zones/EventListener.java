package ch.swisssmp.zones;

import ch.swisssmp.resourcepack.PlayerResourcePackUpdateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

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
}
