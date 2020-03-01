package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class Grindstone extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.GRINDSTONE;
	}

	@Override
	protected String GetSubPermission() {
		return "grindstone";
	}
}
