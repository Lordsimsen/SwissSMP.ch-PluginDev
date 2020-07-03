package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class Anvil extends BlockInteractPrevention {

	@Override
	protected boolean isMatch(Block block) {
		return block.getType()==Material.ANVIL || block.getType()==Material.CHIPPED_ANVIL || block.getType()==Material.DAMAGED_ANVIL;
	}

	@Override
	protected String getSubPermission() {
		return "anvil";
	}
}
