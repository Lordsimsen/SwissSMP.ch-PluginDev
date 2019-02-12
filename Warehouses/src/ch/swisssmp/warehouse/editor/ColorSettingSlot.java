package ch.swisssmp.warehouse.editor;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.SelectSlot;
import ch.swisssmp.warehouse.filters.FilterSetting;
import ch.swisssmp.warehouse.filters.FilterSettings;

public class ColorSettingSlot extends SelectSlot {

	private static final FilterSetting[] values = new FilterSetting[]{
		FilterSetting.Default,
		FilterSetting.Any,
		FilterSetting.Include
	};
	private final FilterSettings settings;
	
	public ColorSettingSlot(CustomEditorView view, int slot, FilterSettings settings) {
		super(view, slot);
		this.settings = settings;
	}

	@Override
	protected int getInitialValue() {
		return getIndex(settings.color);
	}

	@Override
	protected int getOptionsLength() {
		return values.length;
	}

	@Override
	protected void onValueChanged(int arg0) {
		settings.color = values[arg0];
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		CustomItemBuilder result = new CustomItemBuilder();
		switch(settings.color){
		case Default: result.setMaterial(Material.GRAY_WOOL);break;
		case Include:result.setMaterial(Material.ORANGE_WOOL);break;
		case Any:
		default: result.setMaterial(Material.PURPLE_WOOL);break;
		}
		return result;
	}

	@Override
	public String getName() {
		return ChatColor.AQUA+"Farbe";
	}
	
	@Override
	protected List<String> getValueDisplay(){
		String valueDisplay;
		switch(settings.color){
		case Default:valueDisplay = "Standard";break;
		case Include:valueDisplay = "Nur gleiche Farbe";break;
		default:valueDisplay = "Alle Farben";break;
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
