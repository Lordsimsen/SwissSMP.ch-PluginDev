package ch.swisssmp.antiguest.preventions.entities;

import org.bukkit.entity.EntityType;

public class ItemFrame extends EntityInteractPrevention {

	@Override
	protected EntityType GetType() {
		return EntityType.ITEM_FRAME;
	}

	@Override
	protected String GetSubPermission() {
		return "item_frame";
	}
}
