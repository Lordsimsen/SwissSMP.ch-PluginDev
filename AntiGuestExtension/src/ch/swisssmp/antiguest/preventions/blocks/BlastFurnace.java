package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class BlastFurnace extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.BLAST_FURNACE;
	}

	@Override
	protected String GetSubPermission() {
		return "blast_furnace";
	}
}
