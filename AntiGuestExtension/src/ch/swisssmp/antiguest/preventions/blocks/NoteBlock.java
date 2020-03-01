package ch.swisssmp.antiguest.preventions.blocks;

import org.bukkit.Material;

public class NoteBlock extends BlockInteractPrevention {

	@Override
	protected Material GetType() {
		return Material.NOTE_BLOCK;
	}

	@Override
	protected String GetSubPermission() {
		return "note_block";
	}
}
