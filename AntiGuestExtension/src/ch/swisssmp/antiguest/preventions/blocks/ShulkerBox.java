package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class ShulkerBox extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.SHULKER_BOX;
	}

	@Override
	protected String GetSubPermission() {
		return "shulker_box";
	}
}
