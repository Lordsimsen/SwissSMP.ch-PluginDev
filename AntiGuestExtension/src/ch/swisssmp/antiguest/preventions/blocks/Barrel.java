package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class Barrel extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.BARREL;
	}

	@Override
	protected String GetSubPermission() {
		return "barrel";
	}
}
