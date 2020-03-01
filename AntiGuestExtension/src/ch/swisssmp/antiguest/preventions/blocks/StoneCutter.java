package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class StoneCutter extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.STONECUTTER;
	}

	@Override
	protected String GetSubPermission() {
		return "stone_cutter";
	}
}
