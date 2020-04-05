package ch.swisssmp.editor.slot;

import java.util.List;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;

public class GenericValueRangeSlot extends ValueRangeSlot {

	private final ValueCallback callback;
	private String name;
	private List<String> description;
	private float initialValue = 0;
	private float step = 0.1f;
	private float bigStep = 0.25f;
	private float minValue = 0;
	private float maxValue = 1;
	private boolean complete = true;
	
	private CustomItemBuilder slotBase;
	
	public GenericValueRangeSlot(CustomEditorView view, int slot, CustomItemBuilder slotBase, ValueCallback callback) {
		super(view, slot);
		this.slotBase = slotBase;
		this.callback = callback;
	}
	
	public void setInitialValue(float initialValue) {
		this.initialValue = initialValue;
	}
	
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	
	public void setStep(float step) {
		this.step = step;
	}
	
	public void setBigStep(float bigStep) {
		this.bigStep = bigStep;
	}
	
	public void setLimits(float min, float max) {
		minValue = min;
		maxValue = max;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDescription(List<String> description) {
		this.description = description;
	}

	@Override
	protected float getInitialValue() {
		return initialValue;
	}

	@Override
	protected float getMinValue() {
		return minValue;
	}

	@Override
	protected float getMaxValue() {
		return maxValue;
	}

	@Override
	protected float getStep() {
		return step;
	}

	@Override
	protected float getBigStep() {
		return bigStep;
	}

	@Override
	protected void onValueChanged(float value) {
		callback.onValueChanged(value);
	}

	@Override
	protected boolean isComplete() {
		return complete;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	protected List<String> getNormalDescription() {
		return description;
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		return slotBase;
	}

	public interface ValueCallback{
		void onValueChanged(float value);
	}
}
