package ch.swisssmp.warehouse.editor;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.SelectSlot;
import ch.swisssmp.warehouse.filters.FilterSetting;
import ch.swisssmp.warehouse.filters.FilterSettings;

public class PotionSettingSlot extends SelectSlot {

	private static final FilterSetting[] values = new FilterSetting[]{
		FilterSetting.Default,
		FilterSetting.Any,
		FilterSetting.Exact
	};
	private final FilterSettings settings;
	
	public PotionSettingSlot(CustomEditorView view, int slot, FilterSettings settings) {
		super(view, slot);
		this.settings = settings;
	}

	@Override
	protected int getInitialValue() {
		return getIndex(settings.potion);
	}

	@Override
	protected int getOptionsLength() {
		return values.length;
	}

	@Override
	protected void onValueChanged(int arg0) {
		settings.potion = values[arg0];
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		CustomItemBuilder result = new CustomItemBuilder();
		result.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		result.setMaterial(Material.POTION);
		result.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);
		switch(settings.potion){
		case Default: break;
		case Exact:result.addEnchantment(Enchantment.DURABILITY, 1, true);break;
		case Any:
		default: break;
		}
		return result;
	}

	@Override
	public String getName() {
		return ChatColor.AQUA+"Tranksorten";
	}
	
	@Override
	protected List<String> getValueDisplay(){
		String valueDisplay;
		switch(settings.potion){
		case Default:valueDisplay = "Standard";break;
		case Exact:valueDisplay = "Nur gleiche Sorte";break;
		default:valueDisplay = "Alle Sorten";break;
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
