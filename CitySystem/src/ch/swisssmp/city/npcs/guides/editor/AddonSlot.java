package ch.swisssmp.city.npcs.guides.editor;

import java.util.List;

import ch.swisssmp.city.Addon;
import ch.swisssmp.city.AddonType;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.InfoSlot;

public class AddonSlot extends InfoSlot {

	private final Addon addon;
	private final AddonType type;
	
	public AddonSlot(CustomEditorView view, int slot, Addon addon) {
		super(view, slot);
		this.addon = addon;
		this.type = addon.getType();
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		return type.getItemBuilder();
	}

	@Override
	public String getName() {
		return addon.getState().getColor()+ type.getName();
	}

	@Override
	protected List<String> getNormalDescription() {
		return type.getShortDescription();
	}

	@Override
	protected boolean isComplete() {
		return true;
	}

}
