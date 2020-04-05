package ch.swisssmp.ageofempires.editor;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;

import ch.swisssmp.ageofempires.PlayerSettings;
import ch.swisssmp.ageofempires.TauntSetting;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.SelectSlot;

public class ToggleTauntsSlot extends SelectSlot {

	public ToggleTauntsSlot(CustomEditorView view, int slot) {
		super(view, slot);

	}

	@Override
	protected int getInitialValue() {
		return PlayerSettings.get(this.getView().getPlayer())==TauntSetting.ALLOW ? 1 : 0;
	}

	@Override
	protected int getOptionsLength() {
		return 2;
	}

	@Override
	protected void onValueChanged(int value) {
		PlayerSettings.set(this.getView().getPlayer(), value==1 ? TauntSetting.ALLOW : TauntSetting.MUTE);
	}

	@Override
	public String getName() {
		return this.getValue()==1 ? "Taunts stummschalten" : "Taunts aktivieren";
	}

	@Override
	protected List<String> getNormalDescription() {
		return Arrays.asList("Klicke, um Audiowiedergabe", "an oder abzuschalten.");
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		CustomItemBuilder result = new CustomItemBuilder();
		result.setMaterial(getValue()==1 ? Material.JACK_O_LANTERN : Material.CARVED_PUMPKIN);
		return result;
	}

}
