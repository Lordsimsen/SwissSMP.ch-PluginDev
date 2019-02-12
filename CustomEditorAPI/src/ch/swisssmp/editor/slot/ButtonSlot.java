package ch.swisssmp.editor.slot;

import org.bukkit.event.inventory.ClickType;

import ch.swisssmp.editor.CustomEditorView;

public abstract class ButtonSlot extends EditorSlot {

	public ButtonSlot(CustomEditorView view, int slot) {
		super(view, slot);
	}

	@Override
	public void createItem() {
		this.setItem(this.createSlot());
	}

	@Override
	public boolean onClick(ClickType clickType) {
		if(!this.isComplete()) return true;
		this.triggerOnClick(clickType);
		return true;
	}
	
	protected abstract void triggerOnClick(ClickType clickType);
}
