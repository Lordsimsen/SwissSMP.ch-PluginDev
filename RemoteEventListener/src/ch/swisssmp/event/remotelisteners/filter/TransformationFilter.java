package ch.swisssmp.event.remotelisteners.filter;

import org.bukkit.Bukkit;

import ch.swisssmp.event.pluginlisteners.EventListenerMaster;
import ch.swisssmp.transformations.TransformationEvent;
import ch.swisssmp.utils.ConfigurationSection;

public interface TransformationFilter {
	public default boolean checkTransformation(ConfigurationSection dataSection, TransformationEvent event){
		boolean result = true;
		if(dataSection.contains("transformation_id")){
			if(EventListenerMaster.getInst().debugOn()){
				Bukkit.getLogger().info("[RemoteEventListener] Vergleiche "+dataSection.getInt("transformation_id")+" mit "+event.getArea().getUniqueId());
			}
			result &= dataSection.getInt("transformation_id")==event.getArea().getUniqueId();
		}
		if(dataSection.contains("schematic_name")){
			if(EventListenerMaster.getInst().debugOn()){
				Bukkit.getLogger().info("[RemoteEventListener] Vergleiche "+dataSection.getString("schematic_name")+" mit "+event.getNewState().getSchematicName());
			}
			result &= dataSection.getString("schematic_name").equals(event.getNewState().getSchematicName());
		}
		return result;
	}
}
