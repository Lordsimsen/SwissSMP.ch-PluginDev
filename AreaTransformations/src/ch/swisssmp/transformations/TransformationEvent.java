package ch.swisssmp.transformations;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class TransformationEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final TransformationArea area;
    private final AreaState newState;
    private final World world;
    private final Player player;
    private boolean cancelled = false;
    
    TransformationEvent(TransformationArea area, AreaState newState, World world, Player player){
    	this.area = area;
    	this.newState = newState;
    	this.world = world;
    	this.player = player;
    }
    
    public TransformationArea getArea(){
    	return this.area;
    }
    
    public AreaState getNewState(){
    	return this.newState;
    }
    
    public World getWorld(){
    	return this.world;
    }
    
    public Player getPlayer(){
    	return this.player;
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
