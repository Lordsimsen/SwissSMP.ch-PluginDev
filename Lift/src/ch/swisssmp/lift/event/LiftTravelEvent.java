package ch.swisssmp.lift.event;

import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

import ch.swisssmp.lift.LiftTravel;

public abstract class LiftTravelEvent extends EntityEvent {

	private static final HandlerList handlers = new HandlerList();

	private final LiftTravel travel;
	
	public LiftTravelEvent(Entity entity, LiftTravel travel){
		super(entity);
		this.travel = travel;
	}
	
	public LiftTravel getTravel(){
		return travel;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
    
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
