package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class NoteBlock extends BlockInteractPrevention {

	@Override
	protected boolean isMatch(Block block) {
		return block.getType()==Material.NOTE_BLOCK;
	}

	@Override
	protected String getSubPermission() {
		return "note_block";
	}
}
