package ch.swisssmp.city;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

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
	protected Collection<EditorSlot> initializeEditor() {
		Collection<EditorSlot> result = new ArrayList<EditorSlot>();
		int index = 0;
		for(CitizenInfo citizen : city.getCitizens()){
			result.add(new CitizenSlot(this,index,citizen));
			index++;
		}
		return result;
	}

	@Override
	public String getTitle() {
		return city.getName();
	}

	@Override
	protected int getInventorySize() {
		return Mathf.ceilToInt(city.getCitizens().size()/9f)*9;
	}

	public static CityView open(Player player, UUID cityId){
		City city = CitySystem.getCity(cityId).orElse(null);
		if(city==null) return null;
		return open(player, city);
	}
	
	public static CityView open(Player player, City city){
		CityView result = new CityView(player, city);
		result.open();
		return result;
	}
}
