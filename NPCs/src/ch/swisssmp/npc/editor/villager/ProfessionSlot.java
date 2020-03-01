package ch.swisssmp.npc.editor.villager;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.SelectSlot;
import ch.swisssmp.npc.NPCs;

public class ProfessionSlot extends SelectSlot {

	private final static Profession[] professions = new Profession[]{
			Profession.FARMER,
			Profession.NITWIT,
			Profession.ARMORER,
			Profession.BUTCHER,
			Profession.CARTOGRAPHER,
			Profession.CLERIC,
			Profession.FISHERMAN,
			Profession.FLETCHER,
			Profession.LEATHERWORKER,
			Profession.LIBRARIAN,
			Profession.MASON,
			Profession.SHEPHERD,
			Profession.TOOLSMITH,
			Profession.WEAPONSMITH,
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
		Profession profession = professions[arg0];
		System.out.println("Profession: "+profession);
		villager.setVillagerLevel(5);
		villager.setProfession(profession);
		System.out.println("Applied Profession: "+villager.getProfession());
		Bukkit.getScheduler().runTaskLater(NPCs.getInstance(), ()->{
			System.out.println("Current Profession: "+villager.getProfession());
		}, 2L);
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
		case ARMORER: return "Rüstungsschmied";
		case BUTCHER: return "Metzger";
		case CARTOGRAPHER: return "Kartografe";
		case CLERIC: return "Kleriker";
		case FISHERMAN: return "Fischer";
		case FLETCHER: return "Pfeilmacher";
		case LEATHERWORKER: return "Lederhandwerker";
		case LIBRARIAN: return "Gelehrter";
		case MASON: return "Maurer";
		case NITWIT: return "Nichtsnutz";
		case SHEPHERD: return "Hirt";
		case TOOLSMITH: return "Werkzeugschmied";
		case WEAPONSMITH: return "Waffenschmied";
		case FARMER:
		default: return "Bauer";
		}
	}
	
	private static Material getMaterial(Profession profession){
		switch(profession){
		case ARMORER: return Material.BLACK_WOOL;
		case BUTCHER: return Material.RED_WOOL;
		case CARTOGRAPHER: return Material.PINK_WOOL;
		case CLERIC: return Material.PURPLE_WOOL;
		case FISHERMAN: return Material.CYAN_WOOL;
		case FLETCHER: return Material.LIGHT_GRAY_WOOL;
		case LEATHERWORKER: return Material.BROWN_WOOL;
		case LIBRARIAN: return Material.BLUE_WOOL;
		case MASON: return Material.ORANGE_WOOL;
		case NITWIT: return Material.GREEN_WOOL;
		case SHEPHERD: return Material.WHITE_WOOL;
		case TOOLSMITH: return Material.YELLOW_WOOL;
		case WEAPONSMITH: return Material.GRAY_WOOL;
		case FARMER: return Material.LIME_WOOL;
		default: return Material.MAGENTA_WOOL;
		}
	}
}
