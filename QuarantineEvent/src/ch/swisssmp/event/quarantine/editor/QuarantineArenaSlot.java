package ch.swisssmp.event.quarantine.editor;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.PickItemSlot;
import ch.swisssmp.event.quarantine.QuarantineArena;

public class QuarantineArenaSlot extends PickItemSlot {

	private final QuarantineArena arena;
	
	public QuarantineArenaSlot(CustomEditorView view, int slot, QuarantineArena arena) {
		super(view, slot);
		this.arena = arena;
	}

	@Override
	protected ItemStack createPick() {
		return arena.getItemStack();
	}

	@Override
	protected boolean isComplete() {
		return arena.isReady();
	}

	@Override
	public String getName() {
		String name = arena.getName();
		return name!=null && !name.isEmpty() ? name : "Unbenannte Arena";
	}

	@Override
	protected List<String> getNormalDescription() {
		return arena.getDescription();
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		CustomItemBuilder result = new CustomItemBuilder();
		result.setCustomEnum(QuarantineArena.CustomEnum);
		return result;
	}

}
