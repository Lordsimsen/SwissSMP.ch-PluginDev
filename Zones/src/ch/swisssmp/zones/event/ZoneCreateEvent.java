package ch.swisssmp.zones.event;

import ch.swisssmp.zones.Zone;
import org.bukkit.event.HandlerList;

public class ZoneCreateEvent extends ZoneEvent {
    private static final HandlerList handlers = new HandlerList();

    public ZoneCreateEvent(Zone zone){
        super(zone);
    }

    @Override
    public HandlerList getHandlers() {
        return null;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }
}
