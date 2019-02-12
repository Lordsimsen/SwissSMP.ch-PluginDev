package ch.swisssmp.editor.slot;

import org.bukkit.event.inventory.ClickType;

import ch.swisssmp.editor.CustomEditorView;

public abstract class InfoSlot extends EditorSlot {

	public InfoSlot(CustomEditorView view, int slot) {
		super(view, slot);
	}

	@Override
	public void createItem() {
		this.setItem(this.createSlot());
	}

	@Override
	public boolean onClick(ClickType clickType) {
		return true;
	}
}
