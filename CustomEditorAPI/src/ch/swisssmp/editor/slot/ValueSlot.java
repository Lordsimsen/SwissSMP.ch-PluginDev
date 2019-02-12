package ch.swisssmp.editor.slot;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.editor.CustomEditorView;

/**
 * A slot where you assign a value by placing an ItemStack
 * Or pick the value by clicking with an empty cursor
 * @author Oliver
 *
 */
public abstract class ValueSlot extends EditorSlot {

	public ValueSlot(CustomEditorView view, int slot) {
		super(view, slot);
	}

	@Override
	public void createItem() {
		this.setItem(this.createSlot());
	}

	@Override
	public boolean onClick(ClickType clickType) {
		if(this.getView().isCursorEmpty()){
			this.setItem(this.createPick());
			this.setItemLater(this.createSlot());
			return false;
		}
		else{
			boolean result = this.applyValue(this.getView().getCursor());
			if(result){
				this.getView().clearCursorLater();
				this.setItem(this.createSlot());
			}
			return true;
		}
	}

	protected abstract ItemStack createPick();
	protected abstract boolean applyValue(ItemStack itemStack);
}
