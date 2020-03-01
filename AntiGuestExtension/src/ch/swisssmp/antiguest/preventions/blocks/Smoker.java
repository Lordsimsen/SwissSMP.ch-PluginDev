package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class Smoker extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.SMOKER;
	}

	@Override
	protected String GetSubPermission() {
		return "smoker";
	}
}
