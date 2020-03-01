package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

import ch.swisssmp.antiguest.preventions.Prevention;

public abstract class BlockInteractPrevention extends Prevention {
	protected abstract Material GetType();
	protected abstract String GetSubPermission();
	
	public Material GetBlock() {
		return GetType();
	}
}
