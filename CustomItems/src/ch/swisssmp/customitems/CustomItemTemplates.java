package ch.swisssmp.customitems;

import java.util.Collection;
import java.util.HashMap;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import ch.swisssmp.webcore.RequestMethod;

public class CustomItemTemplates {
	protected static HashMap<String,CustomItemTemplate> templates = new HashMap<String,CustomItemTemplate>();
	
	protected static void load(){
		HTTPRequest request = DataSource.getResponse(CustomItems.getInstance(), "get_items.php", RequestMethod.POST_SYNC);
		load(request.getYamlResponse());
	}
	
	protected static void load(YamlConfiguration yamlConfiguration){
		templates.clear();
		if(yamlConfiguration==null || !yamlConfiguration.contains("items")) return;
		ConfigurationSection itemsSection = yamlConfiguration.getConfigurationSection("items");
		for(String key : itemsSection.getKeys(false)){
			CustomItemTemplate itemTemplate = new CustomItemTemplate(itemsSection.getConfigurationSection(key));
			if(itemTemplate.getCustomEnum()==null || itemTemplate.getCustomEnum().isEmpty()) continue;
			templates.put(itemTemplate.getCustomEnum().toLowerCase(), itemTemplate);
		}
	}

	protected static Collection<String> getCustomEnums(){
		return templates.keySet();
	}
}
