package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class TrappedChest extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.TRAPPED_CHEST;
	}

	@Override
	protected String GetSubPermission() {
		return "trapped_chest";
	}
}
