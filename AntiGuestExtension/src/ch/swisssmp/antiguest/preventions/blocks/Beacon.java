package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.block.Block;

public class Beacon extends BlockInteractPrevention {

	@Override
	protected boolean isMatch(Block block) {
		return block.getState() instanceof org.bukkit.block.Beacon;
	}

	@Override
	protected String getSubPermission() {
		return "beacon";
	}
}
