package ch.swisssmp.travel.editor;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;

import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.editor.slot.SelectSlot;
import ch.swisssmp.travel.TravelStationEditor;
import ch.swisssmp.travel.TravelStationType;

public class TravelStationTypeSlot extends SelectSlot {

	private final TravelStationEditor view;
	
	public TravelStationTypeSlot(TravelStationEditor view, int slot) {
		super(view, slot);
		this.view = view;
	}

	@Override
	protected int getOptionsLength() {
		return TravelStationType.values().length;
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		return CustomItems.getCustomItemBuilder(TravelStationType.values()[this.getValue()].getIcon());
	}

	@Override
	public String getName() {
		return ChatColor.AQUA+"Stationstyp";
	}
	
	@Override
	protected List<String> getValueDisplay(){
		return Arrays.asList(TravelStationType.values()[this.getValue()].getName());
	}

	@Override
	protected List<String> getNormalDescription() {
		return Arrays.asList("Klicken um zu ändern");
	}

	@Override
	protected void onValueChanged(int value) {
		this.view.getStation().setStationType(TravelStationType.values()[value]);
	}

	@Override
	protected int getInitialValue(){
		TravelStationType currentType = this.view.getStation().getStationType();
		TravelStationType[] types = TravelStationType.values();
		for(int i = 0; i < types.length; i++){
			if(types[i]!=currentType) continue;
			return i;
		}
		return 0;
	}
}
