package ch.swisssmp.antiguest.preventions.entities;

import org.bukkit.entity.EntityType;

import ch.swisssmp.antiguest.preventions.Prevention;

public abstract class EntityInteractPrevention extends Prevention {
	protected abstract EntityType GetType();
	protected abstract String GetSubPermission();
	
	public EntityType GetEntity() {
		return GetType();
	}
}
