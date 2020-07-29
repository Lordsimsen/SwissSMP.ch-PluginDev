package ch.swisssmp.city.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import ch.swisssmp.city.Citizenship;
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
		Citizenship mayorInfo = city.getCitizenship(city.getMayor());
		if(mayorInfo!=null) result.add("Bürgermeister: "+mayorInfo.getDisplayName());
		Collection<Citizenship> citizenships = city.getCitizenships();
		result.add("Bürger:");
		int limit = 5;
		for(Citizenship citizenship : citizenships){
			String displayString = "- "+ citizenship.getDisplayName();
			if(citizenship.getRole()!=null && !citizenship.getRole().isEmpty()){
				displayString+=ChatColor.ITALIC+" ("+ citizenship.getRole()+")";
			}
			result.add(displayString);
			limit--;
			if(limit<=0) break;
		}
		if(citizenships.size()>5) result.add("Und "+(citizenships.size()-5)+" weitere..");
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
