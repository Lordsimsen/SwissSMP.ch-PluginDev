package ch.swisssmp.event.listeners.filter;

import org.bukkit.World;

import ch.swisssmp.utils.ConfigurationSection;

public interface WorldFilter {
	public default boolean checkWorld(ConfigurationSection dataSection, World world){
		boolean result = true;
		if(dataSection.contains("world")){
			result &= dataSection.getString("world").equals(world.getName());
		}
		return result;
	}
}
