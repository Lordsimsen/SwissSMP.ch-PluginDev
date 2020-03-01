package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class BeeHive extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.BEEHIVE;
	}

	@Override
	protected String GetSubPermission() {
		return "beehive";
	}
}
