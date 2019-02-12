package ch.swisssmp.city.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import ch.swisssmp.city.CitizenInfo;
import ch.swisssmp.city.City;
import ch.swisssmp.city.CityView;
import ch.swisssmp.customitems.CustomItemBuilder;
import ch.swisssmp.customitems.CustomItems;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.ButtonSlot;

public class CitySlot extends ButtonSlot {

	private final City city;
	
	public CitySlot(CustomEditorView view, int slot, City city) {
		super(view, slot);
		this.city = city;
	}

	@Override
	protected CustomItemBuilder createSlotBase() {
		return CustomItems.getCustomItemBuilder(city.getRingType());
	}

	@Override
	public String getName() {
		return ChatColor.AQUA+city.getName();
	}

	@Override
	protected List<String> getNormalDescription() {
		List<String> result = new ArrayList<String>();
		CitizenInfo mayorInfo = city.getCitizen(city.getMayor());
		if(mayorInfo!=null) result.add("Bürgermeister: "+mayorInfo.getDisplayName());
		Collection<CitizenInfo> citizens = city.getCitizens();
		result.add("Bürger:");
		int limit = 5;
		for(CitizenInfo citizen : citizens){
			String displayString = "- "+citizen.getDisplayName();
			if(citizen.getRole()!=null && !citizen.getRole().isEmpty()){
				displayString+=ChatColor.ITALIC+" ("+citizen.getRole()+")";
			}
			result.add(displayString);
			limit--;
			if(limit<=0) break;
		}
		if(citizens.size()>5) result.add("Und "+(citizens.size()-5)+" weitere..");
		return result;
	}

	@Override
	protected boolean isComplete() {
		return true;
	}

	@Override
	protected void triggerOnClick(ClickType arg0) {
		CityView.open((Player) this.getView().getPlayer(), city);
	}

}
