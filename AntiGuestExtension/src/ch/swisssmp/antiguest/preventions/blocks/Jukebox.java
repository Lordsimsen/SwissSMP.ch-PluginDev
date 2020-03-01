package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class Jukebox extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.JUKEBOX;
	}

	@Override
	protected String GetSubPermission() {
		return "jukebox";
	}
}
