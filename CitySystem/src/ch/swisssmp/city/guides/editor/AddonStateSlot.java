package ch.swisssmp.city.guides.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.swisssmp.city.Addon;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.InfoSlot;

public class AddonStateSlot extends InfoSlot{
	
	private final Addon addon;
	
	public AddonStateSlot(CustomEditorView view, int slot, Addon addon) {
		super(view, slot);
		this.addon = addon;
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		CustomItemBuilder builder = new CustomItemBuilder();
		builder.setMaterial(addon.getState().getMaterial());
		builder.setAmount(1);	
		return builder;
	}

	@Override
	public String getName() {
		return addon.getState().getColor()+addon.getState().getDisplayName();
	}

	@Override
	protected List<String> getNormalDescription() {
		String reasonDisplay = addon.getStateReasonMessage();
		List<String> result = new ArrayList<String>();
		if(reasonDisplay!=null) result.addAll(Arrays.asList(reasonDisplay.split("\n")));
		return result;
	}

	@Override
	protected boolean isComplete() {
		return true;
	}
	
}
