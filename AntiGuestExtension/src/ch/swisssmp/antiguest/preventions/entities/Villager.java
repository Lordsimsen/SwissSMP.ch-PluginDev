package ch.swisssmp.antiguest.preventions.entities;

import org.bukkit.entity.EntityType;

public class Villager extends EntityInteractPrevention {

	@Override
	protected EntityType GetType() {
		return EntityType.VILLAGER;
	}

	@Override
	protected String GetSubPermission() {
		return "villager";
	}
}
