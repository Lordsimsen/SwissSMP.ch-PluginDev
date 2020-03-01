package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class Dropper extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.DROPPER;
	}

	@Override
	protected String GetSubPermission() {
		return "dropper";
	}
}
