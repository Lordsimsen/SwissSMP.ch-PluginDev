package ch.swisssmp.zones.event;

import ch.swisssmp.zones.Zone;
import org.bukkit.event.Event;

public abstract class ZoneEvent extends Event {

    private final Zone zone;

    protected ZoneEvent(Zone zone){
        this.zone = zone;
    }

    public Zone getZone(){
        return zone;
    }
}
