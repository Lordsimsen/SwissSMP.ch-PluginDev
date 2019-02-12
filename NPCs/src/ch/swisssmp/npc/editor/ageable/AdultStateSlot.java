package ch.swisssmp.npc.editor.ageable;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Ageable;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.SelectSlot;

public class AdultStateSlot extends SelectSlot {

	private final Ageable ageable;
	
	public AdultStateSlot(CustomEditorView view, int slot, Ageable ageable) {
		super(view, slot);
		this.ageable = ageable;
	}

	@Override
	protected int getInitialValue() {
		return ageable.isAdult() ? 1 : 0;
	}

	@Override
	protected int getOptionsLength() {
		return 2;
	}

	@Override
	protected void onValueChanged(int arg0) {
		if(arg0==1) ageable.setAdult();
		else ageable.setBaby();
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		CustomItemBuilder result = new CustomItemBuilder();
		result.setMaterial(this.getValue()==1 ? Material.COAL_BLOCK : Material.COAL);
		return result;
	}

	@Override
	public String getName() {
		return ChatColor.AQUA+"Alter";
	}

	@Override
	protected List<String> getValueDisplay() {
		if(this.getValue()==1){
			return Arrays.asList("Erwachsen");
		}
		else{
			return Arrays.asList("Baby");
		}
	}

	@Override
	protected List<String> getNormalDescription() {
		return null;
	}
}
