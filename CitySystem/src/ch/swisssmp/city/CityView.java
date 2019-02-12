package ch.swisssmp.city;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

import ch.swisssmp.city.editor.CitizenSlot;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.utils.Mathf;

public class CityView extends CustomEditorView implements Listener {

	private final City city;
	
	private CityView(Player player, City city) {
		super(player);
		this.city = city;
	}

	@Override
	protected Inventory createInventory() {
		return Bukkit.createInventory(null, Mathf.ceilToInt(city.getCitizens().size()/9f)*9, city.getName());
	}

	@Override
	protected Collection<EditorSlot> createSlots() {
		Collection<EditorSlot> result = new ArrayList<EditorSlot>();
		int index = 0;
		for(CitizenInfo citizen : city.getCitizens()){
			result.add(new CitizenSlot(this,index,citizen));
			index++;
		}
		return result;
	}

	public static CityView open(Player player, int city_id){
		City city = City.get(city_id);
		if(city==null) return null;
		return open(player, city);
	}
	
	public static CityView open(Player player, City city){
		CityView result = new CityView(player, city);
		result.open();
		return result;
	}
}
