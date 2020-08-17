package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class Grindstone extends BlockInteractPrevention {

	@Override
	protected boolean isMatch(Block block) {
		return block.getType()==Material.GRINDSTONE;
	}

	@Override
	protected String getSubPermission() {
		return "grindstone";
	}
}
