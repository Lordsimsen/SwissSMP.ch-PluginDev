package ch.swisssmp.warehouse.editor;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.SelectSlot;
import ch.swisssmp.warehouse.filters.FilterSetting;
import ch.swisssmp.warehouse.filters.FilterSettings;

public class DamageSettingSlot extends SelectSlot {

	private static final FilterSetting[] values = new FilterSetting[]{
		FilterSetting.Default,
		FilterSetting.Any,
		FilterSetting.Include,
		FilterSetting.Exclude
	};
	private final FilterSettings settings;
	
	public DamageSettingSlot(CustomEditorView view, int slot, FilterSettings settings) {
		super(view, slot);
		this.settings = settings;
	}

	@Override
	protected int getInitialValue() {
		return getIndex(settings.damage);
	}

	@Override
	protected int getOptionsLength() {
		return values.length;
	}

	@Override
	protected void onValueChanged(int arg0) {
		settings.damage = values[arg0];
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		CustomItemBuilder result = new CustomItemBuilder();
		result.setMaterial(Material.STONE_PICKAXE);
		switch(settings.damage){
		case Exclude:result.setDurability((short) 0);break;
		case Include:result.setDurability((short) 100);break;
		default: result.setDurability((short)(Material.STONE_PICKAXE.getMaxDurability()/2));break;
		}
		result.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		return result;
	}

	@Override
	public String getName() {
		return ChatColor.AQUA+"Beschädigung";
	}
	
	@Override
	protected List<String> getValueDisplay(){
		String valueDisplay;
		switch(settings.damage){
		case Default:valueDisplay = "Standard";break;
		case Include:valueDisplay = "Nur beschädigt";break;
		case Exclude:valueDisplay = "Nur unbeschädigt";break;
		default:valueDisplay = "Egal";break;
		}
		return Arrays.asList(valueDisplay);
	}

	@Override
	protected List<String> getNormalDescription() {
		return null;
	}

	private int getIndex(FilterSetting setting){
		for(int i = 0; i < values.length; i++){
			if(values[i]==setting) return i;
		}
		return 0;
	}
}
