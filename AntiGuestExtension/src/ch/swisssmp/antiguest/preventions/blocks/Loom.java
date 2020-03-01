package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class Loom extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.LOOM;
	}

	@Override
	protected String GetSubPermission() {
		return "loom";
	}
}
