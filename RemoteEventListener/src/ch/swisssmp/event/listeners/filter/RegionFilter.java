package ch.swisssmp.event.listeners.filter;

import com.mewin.WGRegionEvents.events.RegionEvent;

import ch.swisssmp.utils.ConfigurationSection;

public interface RegionFilter {
	public default boolean checkRegion(ConfigurationSection dataSection, RegionEvent event){
		boolean result = true;
		if(dataSection.contains("region_id")){
			result &= dataSection.getString("region_id").equals(event.getRegion().getId());
		}
		return result;
	}
}
