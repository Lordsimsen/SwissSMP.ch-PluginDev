package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class Dispenser extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.DISPENSER;
	}

	@Override
	protected String GetSubPermission() {
		return "dispenser";
	}
}
