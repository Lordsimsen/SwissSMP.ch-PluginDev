package ch.swisssmp.city;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.entity.Player;

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
	protected Collection<EditorSlot> initializeEditor() {
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
		CitiesView result = new CitiesView(player, cities);
		result.open();
		return result;
	}

	@Override
	public String getTitle() {
		return "Städte";
	}

	@Override
	protected int getInventorySize() {
		return Math.max(1, Mathf.ceilToInt(cities.size()/9f))*9;
	}
}
