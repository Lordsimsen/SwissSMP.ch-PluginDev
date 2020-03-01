package ch.swisssmp.lift.event;

import org.bukkit.entity.Entity;

import ch.swisssmp.lift.LiftTravel;

public class LiftExitEvent extends LiftTravelEvent {

	public LiftExitEvent(Entity entity, LiftTravel travel) {
		super(entity, travel);

	}

}
