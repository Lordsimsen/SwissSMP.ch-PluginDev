package ch.swisssmp.city;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import ch.swisssmp.city.editor.CitySlot;
import ch.swisssmp.editor.CustomEditorView;
import ch.swisssmp.editor.slot.EditorSlot;
import ch.swisssmp.utils.Mathf;

public class CitiesView extends CustomEditorView {

	private final Collection<City> cities;
	
	protected CitiesView(Player player, Collection<City> cities) {
		super(player);
		this.cities = cities;
	}

	@Override
	protected Inventory createInventory() {
		return Bukkit.createInventory(null, Mathf.ceilToInt(cities.size()/9f)*9, "Städte");
	}

	@Override
	protected Collection<EditorSlot> createSlots() {
		Collection<EditorSlot> result = new ArrayList<EditorSlot>();
		int slot = 0;
		for(City city : cities){
			result.add(new CitySlot(this, slot, city));
			slot++;
		}
		return result;
	}

	public static CitiesView open(Player player){
		Collection<City> cities = Cities.getAll();
		if(cities.size()==0) return null;
		CitiesView result = new CitiesView(player, cities);
		result.open();
		return result;
	}
}
