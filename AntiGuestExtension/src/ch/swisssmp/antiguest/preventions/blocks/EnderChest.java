package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class EnderChest extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.ENDER_CHEST;
	}

	@Override
	protected String GetSubPermission() {
		return "ender_chest";
	}
}
