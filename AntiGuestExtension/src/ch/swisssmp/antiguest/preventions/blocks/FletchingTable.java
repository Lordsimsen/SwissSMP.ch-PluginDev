package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class FletchingTable extends BlockInteractPrevention {

	@Override
	protected boolean isMatch(Block block) {
		return block.getType()==Material.FLETCHING_TABLE;
	}

	@Override
	protected String getSubPermission() {
		return "fletching_table";
	}
}
