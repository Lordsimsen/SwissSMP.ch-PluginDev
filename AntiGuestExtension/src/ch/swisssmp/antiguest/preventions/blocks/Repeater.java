package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class Repeater extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.REPEATER;
	}

	@Override
	protected String GetSubPermission() {
		return "repeater";
	}
}
