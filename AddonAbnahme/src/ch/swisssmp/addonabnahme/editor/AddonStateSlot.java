package ch.swisssmp.addonabnahme.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.swisssmp.addonabnahme.AddonState;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.InfoSlot;

public class AddonStateSlot extends InfoSlot{
	
	private AddonState state;
	private String reason;
	
	public AddonStateSlot(CustomEditorView view, int slot, AddonState state, String reason) {
		super(view, slot);
		this.state = state;
		this.reason = reason;
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		CustomItemBuilder builder = new CustomItemBuilder();
		builder.setMaterial(state.getMaterial());
		builder.setAmount(1);	
		return builder;
	}

	@Override
	public String getName() {
		return state.getColor()+state.getDisplayName();
	}

	@Override
	protected List<String> getNormalDescription() {
		List<String> result = new ArrayList<String>();
		if(reason!=null) result.addAll(Arrays.asList(reason.split("\n")));
		return result;
	}

	@Override
	protected boolean isComplete() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
