package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class CartographyTable extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.CARTOGRAPHY_TABLE;
	}

	@Override
	protected String GetSubPermission() {
		return "cartography_table";
	}
}
