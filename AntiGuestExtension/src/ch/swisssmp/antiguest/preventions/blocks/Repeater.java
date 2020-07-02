package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class Repeater extends BlockInteractPrevention {

	@Override
	protected boolean isMatch(Block block) {
		return block.getType()==Material.REPEATER;
	}

	@Override
	protected String getSubPermission() {
		return "repeater";
	}
}
