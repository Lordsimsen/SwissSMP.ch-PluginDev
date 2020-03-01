package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class FletchingTable extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.FLETCHING_TABLE;
	}

	@Override
	protected String GetSubPermission() {
		return "fletching_table";
	}
}
