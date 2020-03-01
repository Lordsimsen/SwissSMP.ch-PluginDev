package ch.swisssmp.lift.event;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;

import ch.swisssmp.lift.LiftTravel;

public class LiftEnterEvent extends LiftTravelEvent implements Cancellable {
	
	private boolean cancelled = false;
	
	public LiftEnterEvent(Entity entity, LiftTravel travel) {
		super(entity, travel);
		
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
