package ch.swisssmp.event.listeners.filter;

import ch.swisssmp.adventuredungeons.event.CampEvent;
import ch.swisssmp.utils.ConfigurationSection;

public interface CampFilter{
	public default boolean checkCamp(ConfigurationSection dataSection, CampEvent event){
		boolean result = true;
		if(dataSection.contains("camp_id")){
			result &= dataSection.getInt("camp_id")==event.getCampId();
		}
		return result;
	}
}
