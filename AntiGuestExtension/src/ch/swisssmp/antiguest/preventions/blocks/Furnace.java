package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class Furnace extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.FURNACE;
	}

	@Override
	protected String GetSubPermission() {
		return "furnace";
	}
}
