package ch.swisssmp.event.listeners.filter;

import ch.swisssmp.transformations.TransformationEvent;
import ch.swisssmp.utils.ConfigurationSection;

public interface TransformationFilter {
	public default boolean checkTransformation(ConfigurationSection dataSection, TransformationEvent event){
		boolean result = true;
		if(dataSection.contains("transformation_id")){
			result &= dataSection.getInt("transformation_id")==event.getArea().transformation_id;
		}
		if(dataSection.contains("schematic_name")){
			result &= dataSection.getString("schematic_name").equals(event.getNewState().schematicName);
		}
		return result;
	}
}
