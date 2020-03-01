package ch.swisssmp.antiguest.preventions.entities;

import org.bukkit.entity.EntityType;

public class Pig extends EntityInteractPrevention {
	@Override
	protected EntityType GetType() {
		return EntityType.PIG;
	}

	@Override
	protected String GetSubPermission() {
		return "pig";
	}
}
