package ch.swisssmp.addonabnahme.editor;

import java.util.ArrayList;
import java.util.List;

import ch.swisssmp.addonabnahme.AddonInstanceInfo;
import ch.swisssmp.addonabnahme.AddonManager;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.InfoSlot;

public class AddonSlot extends InfoSlot {

	private final AddonInstanceInfo instance;
	
	public AddonSlot(CustomEditorView view, int slot, AddonInstanceInfo instance) {
		super(view, slot);
		this.instance = instance;
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		return AddonManager.getAddonBuilder(instance.getAddonInfo());
	}

	@Override
	public String getName() {
		return instance.getState().getColor()+instance.getAddonInfo().getName();
	}

	@Override
	protected List<String> getNormalDescription() {
		List<String> result = new ArrayList<String>();
		//result.add("Status: "+instance.getState().getColor()+instance.getState().getDisplayName());
		return result;
	}

	@Override
	protected boolean isComplete() {
		return true;
	}

}
