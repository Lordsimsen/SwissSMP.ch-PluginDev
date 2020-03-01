package ch.swisssmp.antiguest.preventions.entities;

import org.bukkit.entity.EntityType;

public class Horse extends EntityInteractPrevention {
	@Override
	protected EntityType GetType() {
		return EntityType.HORSE;
	}

	@Override
	protected String GetSubPermission() {
		return "horse";
	}
}
