package ch.swisssmp.craftmmo.mmoevent;

import java.util.UUID;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ch.swisssmp.craftmmo.mmoentity.MmoMob;

public class MmoTalkEvent extends Event implements Cancellable{
    private static final HandlerList handlers = new HandlerList();
	
	private final UUID player_uuid;
	private final MmoMob mmoMob;
	private boolean cancelled = false;
	
	public MmoTalkEvent(UUID player_uuid, MmoMob mmoMob){
		this.player_uuid = player_uuid;
		this.mmoMob = mmoMob;
	}
	
	public MmoMob getMmoMob(){
		return this.mmoMob;
	}
	
	public UUID getPlayerUUID(){
		return this.player_uuid;
	}

	@Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
