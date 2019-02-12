package ch.swisssmp.npc.editor.base;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.SelectSlot;
import ch.swisssmp.npc.NPCInstance;

public class SilentSlot extends SelectSlot {

	private final NPCInstance npc;
	
	public SilentSlot(CustomEditorView view, int slot, NPCInstance npc) {
		super(view, slot);
		this.npc = npc;
	}

	@Override
	protected int getInitialValue() {
		return npc.isSilent() ? 1 : 0;
	}

	@Override
	protected int getOptionsLength() {
		return 2;
	}

	@Override
	protected void onValueChanged(int arg0) {
		this.npc.setSilent(arg0==1);
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		CustomItemBuilder result = new CustomItemBuilder();
		result.setMaterial(this.getValue()==1 ? Material.CARVED_PUMPKIN : Material.JACK_O_LANTERN);
		return result;
	}

	@Override
	public String getName() {
		return ChatColor.AQUA+"Stimme";
	}

	@Override
	protected List<String> getValueDisplay() {
		if(this.getValue()==1){
			return Arrays.asList("Stumm");
		}
		else{
			return Arrays.asList("Aktiviert");
		}
	}

	@Override
	protected List<String> getNormalDescription() {
		return null;
	}
}
