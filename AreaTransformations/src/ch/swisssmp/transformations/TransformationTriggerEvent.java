package ch.swisssmp.transformations;

import org.bukkit.entity.Player;

public class TransformationTriggerEvent extends TransformationEvent{

	TransformationTriggerEvent(TransformationArea area, AreaState newState, Player player) {
		super(area, newState, player);
	}

}
