package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class Beacon extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.BEACON;
	}

	@Override
	protected String GetSubPermission() {
		return "beacon";
	}
}
