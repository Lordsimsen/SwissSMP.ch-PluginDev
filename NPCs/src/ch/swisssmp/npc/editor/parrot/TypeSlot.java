package ch.swisssmp.npc.editor.parrot;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Parrot.Variant;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.SelectSlot;

public class TypeSlot extends SelectSlot {

	private final static Variant[] types = new Variant[]{
			Variant.BLUE,
			Variant.CYAN,
			Variant.GRAY,
			Variant.GREEN,
			Variant.RED
	};
	
	private final Parrot parrot;
	
	public TypeSlot(CustomEditorView view, int slot, Parrot parrot) {
		super(view, slot);
		this.parrot = parrot;
	}

	@Override
	protected int getInitialValue() {
		Variant type = parrot.getVariant();
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
		parrot.setVariant(types[arg0]);
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		CustomItemBuilder result = new CustomItemBuilder();
		result.setMaterial(getMaterial(parrot.getVariant()));
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
		return Arrays.asList(getLabel(parrot.getVariant()));
	}

	@Override
	protected List<String> getNormalDescription() {
		return null;
	}

	private static String getLabel(Variant type){
		switch(type){
		case BLUE: return "Blau";
		case CYAN: return "Türkis";
		case GRAY: return "Grau";
		case GREEN: return "Grün";
		case RED:
		default:
			return "Rot";
		}
	}
	
	private static Material getMaterial(Variant type){
		switch(type){
		case BLUE: return Material.BLUE_WOOL;
		case CYAN: return Material.CYAN_WOOL;
		case GRAY: return Material.LIGHT_GRAY_WOOL;
		case GREEN: return Material.GREEN_WOOL;
		case RED:
		default:
			return Material.RED_WOOL;
		}
		}
	}