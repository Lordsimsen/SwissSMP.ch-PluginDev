package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class EnchantingTable extends BlockInteractPrevention {

	@Override
	protected boolean isMatch(Block block) {
		return block.getState() instanceof org.bukkit.block.EnchantingTable;
	}

	@Override
	protected String getSubPermission() {
		return "enchanting_table";
	}
}
