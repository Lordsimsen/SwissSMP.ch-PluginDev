package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class Campfire extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.CAMPFIRE;
	}

	@Override
	protected String GetSubPermission() {
		return "campfire";
	}
}
