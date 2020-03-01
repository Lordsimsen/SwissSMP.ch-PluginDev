package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class Anvil extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.ANVIL;
	}

	@Override
	protected String GetSubPermission() {
		return "anvil";
	}
}
