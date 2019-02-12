package ch.swisssmp.permissionmanager;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerPermissionsChangedEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    
    private final boolean joining;
    
	public PlayerPermissionsChangedEvent(Player who, boolean joining) {
		super(who);
		this.joining = joining;
	}
	
	public boolean isJoining(){
		return joining;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}
}
