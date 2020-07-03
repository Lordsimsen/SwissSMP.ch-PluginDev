package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class CartographyTable extends BlockInteractPrevention {

	@Override
	protected boolean isMatch(Block block) {
		return block.getType()==Material.CARTOGRAPHY_TABLE;
	}

	@Override
	protected String getSubPermission() {
		return "cartography_table";
	}
}
