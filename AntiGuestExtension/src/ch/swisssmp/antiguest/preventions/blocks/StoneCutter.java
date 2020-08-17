package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class StoneCutter extends BlockInteractPrevention {

	@Override
	protected boolean isMatch(Block block) {
		return block.getType()==Material.STONECUTTER;
	}

	@Override
	protected String getSubPermission() {
		return "stone_cutter";
	}
}
