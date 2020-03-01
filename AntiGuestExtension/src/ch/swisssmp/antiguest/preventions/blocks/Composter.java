package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class Composter extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.COMPOSTER;
	}

	@Override
	protected String GetSubPermission() {
		return "composter";
	}
}
