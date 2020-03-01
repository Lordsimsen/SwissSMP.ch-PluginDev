package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class EnchantingTable extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.ENCHANTING_TABLE;
	}

	@Override
	protected String GetSubPermission() {
		return "enchanting_table";
	}
}
