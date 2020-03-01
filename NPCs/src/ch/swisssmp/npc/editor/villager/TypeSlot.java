package ch.swisssmp.npc.editor.villager;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Type;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.SelectSlot;

public class TypeSlot extends SelectSlot {

	private final static Type[] types = new Type[]{
			Type.PLAINS,
			Type.DESERT,
			Type.JUNGLE,
			Type.SAVANNA,
			Type.SNOW,
			Type.SWAMP,
			Type.TAIGA
	};
	
	private final Villager villager;
	
	public TypeSlot(CustomEditorView view, int slot, Villager villager) {
		super(view, slot);
		this.villager = villager;
	}

	@Override
	protected int getInitialValue() {
		Type type = villager.getVillagerType();
		for(int i = 0; i < types.length; i++){
			if(types[i]!=type) continue;
			return i;
		}
		return 0;
	}

	@Override
	protected int getOptionsLength() {
		return types.length;
	}

	@Override
	protected void onValueChanged(int arg0) {
		villager.setVillagerType(types[arg0]);
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		CustomItemBuilder result = new CustomItemBuilder();
		result.setMaterial(getMaterial(villager.getVillagerType()));
		return result;
	}

	@Override
	protected List<String> getIncompleteDescription() {
		return this.getNormalDescription();
	}

	@Override
	public String getName() {
		return ChatColor.AQUA+"Art";
	}

	@Override
	protected List<String> getValueDisplay() {
		return Arrays.asList(getLabel(villager.getVillagerType()));
	}

	@Override
	protected List<String> getNormalDescription() {
		return null;
	}

	private static String getLabel(Type type){
		switch(type){
		case DESERT: return "Wüste";
		case JUNGLE: return "Dschungel";
		case SAVANNA: return "Savanne";
		case SNOW: return "Schnee";
		case SWAMP: return "Sumpf";
		case TAIGA: return "Taiga";
		case PLAINS:
		default: return "Grasland";
		}
	}
	
	private static Material getMaterial(Type type){
		switch(type){
		case DESERT: return Material.YELLOW_WOOL;
		case JUNGLE: return Material.LIME_WOOL;
		case SAVANNA: return Material.BROWN_WOOL;
		case SNOW: return Material.WHITE_WOOL;
		case SWAMP: return Material.CYAN_WOOL;
		case TAIGA: return Material.LIGHT_GRAY_WOOL;
		case PLAINS:
		default: return Material.GREEN_WOOL;
		}
	}
}
