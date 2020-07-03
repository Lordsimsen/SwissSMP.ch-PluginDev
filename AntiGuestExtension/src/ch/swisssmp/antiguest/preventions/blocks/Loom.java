package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class Loom extends BlockInteractPrevention {

	@Override
	protected boolean isMatch(Block block) {
		return block.getType()==Material.LOOM;
	}

	@Override
	protected String getSubPermission() {
		return "loom";
	}
}
