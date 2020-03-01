package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class BeeNest extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.BEE_NEST;
	}

	@Override
	protected String GetSubPermission() {
		return "bee_nest";
	}
}
