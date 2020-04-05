package ch.swisssmp.editor.slot;

import org.bukkit.event.inventory.ClickType;

import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.utils.Mathf;

public abstract class ValueRangeSlot extends EditorSlot {

	private float value;
	
	public ValueRangeSlot(CustomEditorView view, int slot) {
		super(view, slot);
	}

	protected abstract float getInitialValue();
	protected abstract float getMinValue();
	protected abstract float getMaxValue();
	protected abstract float getStep();
	protected abstract float getBigStep();
	protected abstract void onValueChanged(float value);

	@Override
	public void createItem() {
		this.setItem(this.createSlot());
		this.value = getInitialValue();
	}

	@Override
	public boolean onClick(ClickType clickType) {
		float step;
		switch(clickType) {
		case LEFT: step = getStep();break;
		case SHIFT_LEFT: step = getBigStep();break;
		case RIGHT: step = -getStep();break;
		case SHIFT_RIGHT: step = -getBigStep();break;
		default: step = getStep();break;
		}
		float newValue = (float) Mathf.clamp(value + step, getMinValue(), getMaxValue());
		float oldValue = value;
		value = newValue;
		if(Math.abs(oldValue-newValue)>0.0000001f) {
			onValueChanged(value);
		}
		return true;
	}
}
