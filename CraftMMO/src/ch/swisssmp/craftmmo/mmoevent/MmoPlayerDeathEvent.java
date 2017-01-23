package ch.swisssmp.craftmmo.mmoevent;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ch.swisssmp.craftmmo.mmoworld.MmoRegion;

public class MmoPlayerDeathEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    
    public final MmoRegion mmoRegion;
    public final Player player;
    
	public MmoPlayerDeathEvent(MmoRegion mmoRegion, Player player){
		this.mmoRegion = mmoRegion;
		this.player = player;
	}
	
	public Player getPlayer(){
		return this.player;
	}

	@Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
