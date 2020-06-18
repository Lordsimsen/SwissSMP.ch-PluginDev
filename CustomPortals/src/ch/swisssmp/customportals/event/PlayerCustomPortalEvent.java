package ch.swisssmp.customportals.event;

import ch.swisssmp.customportals.CustomPortal;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerCustomPortalEvent extends PlayerTeleportEvent {

    private final CustomPortal portal;

    public PlayerCustomPortalEvent(Player player, CustomPortal portal, Location from, Location to) {
        super(player, from, to, TeleportCause.PLUGIN);
        this.portal = portal;
    }

    public final CustomPortal getPortal(){
        return portal;
    }
}
