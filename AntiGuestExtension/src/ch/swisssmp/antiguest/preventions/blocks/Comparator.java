package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class Comparator extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.COMPARATOR;
	}

	@Override
	protected String GetSubPermission() {
		return "comparator";
	}
}
