package ch.swisssmp.nightskipper;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class EventListener implements Listener {
    @EventHandler
    private void onPlayerSleep(PlayerBedEnterEvent event){
        if(event.getBedEnterResult()!= PlayerBedEnterEvent.BedEnterResult.OK){
            return;
        }

        SleepChecker.start(event.getBed().getWorld(), 101, 0.5f);
    }
}
