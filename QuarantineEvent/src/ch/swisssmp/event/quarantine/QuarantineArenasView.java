package ch.swisssmp.event.quarantine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.entity.Player;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.event.quarantine.editor.QuarantineArenaSlot;
import ch.swisssmp.utils.Mathf;

public class QuarantineArenasView extends CustomEditorView {

	private final QuarantineArena[] arenas;
	
	protected QuarantineArenasView(Player player, QuarantineArena[] arenas) {
		super(player);
		this.arenas = arenas;
	}

	@Override
	protected int getInventorySize() {
		return Math.min(54, Mathf.ceilToInt(Math.max(1, arenas.length/9.0)) * 9);
	}

	@Override
	protected Collection<EditorSlot> initializeEditor() {
		List<EditorSlot> result = new ArrayList<EditorSlot>();
		int slot = 0;
		for(QuarantineArena arena : this.arenas) {
			result.add(new QuarantineArenaSlot(this, slot, arena));
			slot++;
		}
		return result;
	}

	@Override
	public String getTitle() {
		return "Arenen";
	}
	
	public static QuarantineArenasView open(Player player) {
		ArenaContainer container = ArenaContainer.get(player.getWorld());
		QuarantineArena[] arenas = container.getArenas();
		QuarantineArenasView result = new QuarantineArenasView(player, arenas);
		result.open();
		return result;
	}
}
