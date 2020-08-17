package ch.swisssmp.transformations;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class TransformationTriggerEvent extends TransformationEvent{
    private static final HandlerList handlers = new HandlerList();
	TransformationTriggerEvent(AreaTransformation area, TransformationState newState) {
		super(area);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}
}
