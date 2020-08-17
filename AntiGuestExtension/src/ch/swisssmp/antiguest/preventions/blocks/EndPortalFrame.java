package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.EndGateway;

public class EndPortalFrame extends BlockInteractPrevention {

	@Override
	protected boolean isMatch(Block block) {
		return block.getState() instanceof EndGateway;
	}

	@Override
	protected String getSubPermission() {
		return "end_portal_frame";
	}
}
