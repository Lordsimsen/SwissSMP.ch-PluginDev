package ch.swisssmp.editor.slot;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.editor.CustomEditorView;

public abstract class PickItemSlot extends EditorSlot {

	public PickItemSlot(CustomEditorView view, int slot) {
		super(view, slot);
	}

	@Override
	public void createItem() {
		this.setItem(this.createSlot());
	}

	@Override
	public boolean onClick(ClickType clickType) {
		if(!this.getView().isCursorEmpty()){
			return true;
		}
		this.setItem(this.createPick());
		this.setItemLater(this.createSlot());
		return false;
	}

	protected abstract ItemStack createPick();
}
