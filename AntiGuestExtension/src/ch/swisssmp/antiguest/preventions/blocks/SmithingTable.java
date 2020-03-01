package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class SmithingTable extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.SMITHING_TABLE;
	}

	@Override
	protected String GetSubPermission() {
		return "smithing_table";
	}
}
