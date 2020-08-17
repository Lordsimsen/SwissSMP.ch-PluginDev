package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class SmithingTable extends BlockInteractPrevention {

	@Override
	protected boolean isMatch(Block block) {
		return block.getType()==Material.SMITHING_TABLE;
	}

	@Override
	protected String getSubPermission() {
		return "smithing_table";
	}
}
