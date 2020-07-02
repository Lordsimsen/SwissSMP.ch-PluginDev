package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.block.Block;

public class Beehive extends BlockInteractPrevention {

	@Override
	protected boolean isMatch(Block block) {
		return block.getState() instanceof org.bukkit.block.Beehive;
	}

	@Override
	protected String getSubPermission() {
		return "beehive";
	}
}
