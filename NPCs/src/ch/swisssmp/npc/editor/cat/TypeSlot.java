package ch.swisssmp.npc.editor.cat;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Cat.Type;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.SelectSlot;

public class TypeSlot extends SelectSlot {

	private final static Type[] types = new Type[]{
			Type.ALL_BLACK,
			Type.BLACK,
			Type.BRITISH_SHORTHAIR,
			Type.CALICO,
			Type.JELLIE,
			Type.PERSIAN,
			Type.RAGDOLL,
			Type.RED,
			Type.SIAMESE,
			Type.TABBY,
			Type.WHITE
	};
	
	private final Cat cat;
	
	public TypeSlot(CustomEditorView view, int slot, Cat cat) {
		super(view, slot);
		this.cat = cat;
	}

	@Override
	protected int getInitialValue() {
		Type type = cat.getCatType();
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
		cat.setCatType(types[arg0]);
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		CustomItemBuilder result = new CustomItemBuilder();
		result.setMaterial(getMaterial(cat.getCatType()));
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
		return Arrays.asList(getLabel(cat.getCatType()));
	}

	@Override
	protected List<String> getNormalDescription() {
		return null;
	}

	private static String getLabel(Type type){
		switch(type){
		case ALL_BLACK: return "Schwarz";
		case BLACK: return "Panther";
		case BRITISH_SHORTHAIR: return "Britisch-Kurzhaar";
		case CALICO: return "Calico";
		case JELLIE: return "Jellie";
		case PERSIAN: return "Perser";
		case RAGDOLL: return "Ragdoll";
		case RED: return "Rot";
		case SIAMESE: return "Siam";
		case TABBY: return "Getigert";
		case WHITE:
		default: return "Weiss";
		}
	}
	
	private static Material getMaterial(Type type){
		switch(type){
		case ALL_BLACK: return Material.ENDERMAN_SPAWN_EGG;
		case BLACK: return Material.PANDA_SPAWN_EGG;
		case BRITISH_SHORTHAIR: return Material.ELDER_GUARDIAN_SPAWN_EGG;
		case CALICO: return Material.LLAMA_SPAWN_EGG;
		case JELLIE: return Material.POLAR_BEAR_SPAWN_EGG;
		case PERSIAN: return Material.COD_SPAWN_EGG;
		case RAGDOLL: return Material.SHEEP_SPAWN_EGG;
		case RED: return Material.TROPICAL_FISH_SPAWN_EGG;
		case SIAMESE: return Material.HUSK_SPAWN_EGG;
		case TABBY: return Material.COW_SPAWN_EGG;
		case WHITE:
		default: return Material.GHAST_SPAWN_EGG;
		}
	}
}