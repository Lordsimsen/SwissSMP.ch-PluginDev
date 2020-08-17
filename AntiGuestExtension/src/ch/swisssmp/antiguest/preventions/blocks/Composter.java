package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class Composter extends BlockInteractPrevention {

	@Override
	protected boolean isMatch(Block block) {
		return block.getType()==Material.COMPOSTER;
	}

	@Override
	protected String getSubPermission() {
		return "composter";
	}
}
