package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class Chest extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.CHEST;
	}

	@Override
	protected String GetSubPermission() {
		return "chest";
	}
}
