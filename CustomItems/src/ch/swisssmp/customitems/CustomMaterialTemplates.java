package ch.swisssmp.customitems;

import java.util.HashMap;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import ch.swisssmp.webcore.RequestMethod;

public class CustomMaterialTemplates {
	protected static HashMap<String,CustomMaterialTemplate> templates = new HashMap<String,CustomMaterialTemplate>();
	
	protected static void load(){
		HTTPRequest request = DataSource.getResponse(CustomItems.getInstance(), "get_materials.php", RequestMethod.POST_SYNC);
		load(request.getYamlResponse());
	}
	
	protected static void load(YamlConfiguration yamlConfiguration){
		templates.clear();
		if(yamlConfiguration==null || !yamlConfiguration.contains("materials")) return;
		ConfigurationSection materialsSection = yamlConfiguration.getConfigurationSection("materials");
		for(String key : materialsSection.getKeys(false)){
			CustomMaterialTemplate materialTemplate = new CustomMaterialTemplate(materialsSection.getConfigurationSection(key));
			if(materialTemplate.getCustomEnum()==null || materialTemplate.getCustomEnum().isEmpty()) continue;
			templates.put(materialTemplate.getCustomEnum().toLowerCase(), materialTemplate);
		}
	}
}
