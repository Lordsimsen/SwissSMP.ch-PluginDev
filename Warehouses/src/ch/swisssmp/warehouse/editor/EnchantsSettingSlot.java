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

public class EnchantsSettingSlot extends SelectSlot {

	private static final FilterSetting[] values = new FilterSetting[]{
		FilterSetting.Default,
		FilterSetting.Any,
		FilterSetting.Include,
		FilterSetting.Exclude
	};
	private final FilterSettings settings;
	
	public EnchantsSettingSlot(CustomEditorView view, int slot, FilterSettings settings) {
		super(view, slot);
		this.settings = settings;
	}

	@Override
	protected int getInitialValue() {
		return getIndex(settings.enchantments);
	}

	@Override
	protected int getOptionsLength() {
		return values.length;
	}

	@Override
	protected void onValueChanged(int arg0) {
		settings.enchantments = values[arg0];
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		CustomItemBuilder result = new CustomItemBuilder();
		result.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		
		switch(settings.enchantments){
		case Exclude:result.setMaterial(Material.BOOK);break;
		default: result.setMaterial(Material.ENCHANTED_BOOK);break;
		}
		return result;
	}

	@Override
	public String getName() {
		return ChatColor.AQUA+"Verzauberungen";
	}
	
	@Override
	protected List<String> getValueDisplay(){
		String valueDisplay;
		switch(settings.enchantments){
		case Default:valueDisplay = "Standard";break;
		case Include:valueDisplay = "Nur verzaubert";break;
		case Exclude:valueDisplay = "Nur unverzaubert";break;
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
