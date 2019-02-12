package ch.swisssmp.npc.editor.villager;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.SelectSlot;

public class ProfessionSlot extends SelectSlot {

	private final static Profession[] professions = new Profession[]{
			Profession.BLACKSMITH,
			Profession.BUTCHER,
			Profession.FARMER,
			Profession.LIBRARIAN,
			Profession.NITWIT,
			Profession.PRIEST
	};
	
	private final Villager villager;
	
	public ProfessionSlot(CustomEditorView view, int slot, Villager villager) {
		super(view, slot);
		this.villager = villager;
	}

	@Override
	protected int getInitialValue() {
		Profession profession = villager.getProfession();
		for(int i = 0; i < professions.length; i++){
			if(professions[i]!=profession) continue;
			return i;
		}
		return 0;
	}

	@Override
	protected int getOptionsLength() {
		return professions.length;
	}

	@Override
	protected void onValueChanged(int arg0) {
		villager.setProfession(professions[arg0]);
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		CustomItemBuilder result = new CustomItemBuilder();
		result.setMaterial(getMaterial(villager.getProfession()));
		return result;
	}

	@Override
	protected List<String> getIncompleteDescription() {
		return this.getNormalDescription();
	}

	@Override
	public String getName() {
		return ChatColor.AQUA+"Beruf";
	}

	@Override
	protected List<String> getValueDisplay() {
		return Arrays.asList(getLabel(villager.getProfession()));
	}

	@Override
	protected List<String> getNormalDescription() {
		return null;
	}

	private static String getLabel(Profession profession){
		switch(profession){
		case BLACKSMITH: return "Schmied";
		case BUTCHER: return "Metzger";
		case LIBRARIAN: return "Gelehrter";
		case NITWIT: return "Nichtsnutz";
		case PRIEST: return "Priester";
		case FARMER:
		default: return "Bauer";
		}
	}
	
	private static Material getMaterial(Profession profession){
		switch(profession){
		case BLACKSMITH: return Material.BLACK_WOOL;
		case BUTCHER: return Material.RED_WOOL;
		case FARMER: return Material.BROWN_WOOL;
		case LIBRARIAN: return Material.WHITE_WOOL;
		case NITWIT: return Material.GREEN_WOOL;
		case PRIEST: return Material.PURPLE_WOOL;
		default: return Material.PINK_WOOL;
		}
	}
}
