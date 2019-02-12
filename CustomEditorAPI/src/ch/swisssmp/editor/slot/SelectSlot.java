package ch.swisssmp.editor.slot;

import org.bukkit.event.inventory.ClickType;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.utils.Mathf;

public abstract class SelectSlot extends EditorSlot {

	private int defaultValue = 0;
	private int current;
	
	public SelectSlot(CustomEditorView view, int slot) {
		super(view, slot);
	}

	@Override
	public void createItem() {
		this.current = this.getInitialValue();
		this.current = Mathf.wrap(current, this.getOptionsLength());
		this.setItem(this.createSlot());
	}

	@Override
	public boolean onClick(ClickType clickType) {
		if(clickType==ClickType.RIGHT){
			current--;
		}
		else if(clickType==ClickType.MIDDLE){
			current = this.defaultValue;
		}
		else{
			current++;
		}
		current = Mathf.wrap(current, this.getOptionsLength());
		this.onValueChanged(current);
		this.setItem(this.createSlot());
		return true;
	}

	@Override
	protected boolean isComplete() {
		return true;
	}
	
	public void setDefaultValue(int defaultValue){
		this.defaultValue = defaultValue;
	}
	
	protected int getValue(){
		return current;
	}
	
	protected abstract int getInitialValue();
	protected abstract int getOptionsLength();
	
	protected abstract void onValueChanged(int value);
}
