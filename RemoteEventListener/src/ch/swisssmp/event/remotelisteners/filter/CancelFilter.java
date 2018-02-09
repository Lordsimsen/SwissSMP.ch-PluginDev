package ch.swisssmp.event.remotelisteners.filter;

import org.bukkit.event.Cancellable;

import ch.swisssmp.utils.ConfigurationSection;

public interface CancelFilter {
	public default boolean checkCancelled(ConfigurationSection dataSection, Cancellable event){
		boolean result = true;
		if(dataSection.contains("is_cancelled")){
			result &= (dataSection.getBoolean("is_cancelled")==event.isCancelled());
		}
		return result;
	}
}
