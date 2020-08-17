package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

import ch.swisssmp.antiguest.preventions.Prevention;
import org.bukkit.block.Block;

public abstract class BlockInteractPrevention extends Prevention {
	protected abstract boolean isMatch(Block block);
	protected abstract String getSubPermission();
}
