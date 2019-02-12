package ch.swisssmp.npc.editor.base;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.SelectSlot;
import ch.swisssmp.npc.NPCInstance;

public class NameVisibilitySlot extends SelectSlot {

	private final NPCInstance npc;
	
	public NameVisibilitySlot(CustomEditorView view, int slot, NPCInstance npc) {
		super(view, slot);
		this.npc = npc;
	}

	@Override
	protected int getInitialValue() {
		return npc.isNameVisible() ? 1 : 0;
	}

	@Override
	protected int getOptionsLength() {
		return 2;
	}

	@Override
	protected void onValueChanged(int arg0) {
		this.npc.setNameVisible(arg0==1);
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		CustomItemBuilder result = new CustomItemBuilder();
		result.setMaterial(this.getValue()==1 ? Material.ENDER_EYE : Material.ENDER_PEARL);
		return result;
	}

	@Override
	public String getName() {
		return ChatColor.AQUA+"Anzeigemodus";
	}

	@Override
	protected List<String> getValueDisplay() {
		if(this.getValue()==1){
			return Arrays.asList("Name immer anzeigen");
		}
		else{
			return Arrays.asList("Name nur beim", "betrachten anzeigen");
		}
	}

	@Override
	protected List<String> getNormalDescription() {
		return null;
	}
}
