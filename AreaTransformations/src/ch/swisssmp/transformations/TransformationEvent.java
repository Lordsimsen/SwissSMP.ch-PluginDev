package ch.swisssmp.transformations;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class TransformationEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final AreaTransformation area;
    private boolean cancelled = false;
    
    TransformationEvent(AreaTransformation area){
    	this.area = area;
    }
    
    public AreaTransformation getArea(){
    	return this.area;
    }
    
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		this.cancelled = arg0;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList(){
		return handlers;
	}

}
