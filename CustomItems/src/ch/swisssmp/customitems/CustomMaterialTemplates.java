package ch.swisssmp.customitems;

import java.util.Collection;
import java.util.HashMap;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import ch.swisssmp.webcore.RequestMethod;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CustomMaterialTemplates {
	protected static HashMap<String,CustomMaterialTemplate> templates = new HashMap<String,CustomMaterialTemplate>();
	
	protected static void load(){
		HTTPRequest request = DataSource.getResponse(CustomItemsPlugin.getInstance(), "get_materials.php", RequestMethod.POST_SYNC);
		load(request.getJsonResponse());
	}
	
	protected static void load(JsonObject json){
		templates.clear();
		if(json==null || !json.has("materials")) return;
		JsonArray materialsArray = json.getAsJsonArray("materials");
		for(JsonElement element : materialsArray){
			if(!element.isJsonObject()) continue;
			CustomMaterialTemplate materialTemplate = new CustomMaterialTemplate(element.getAsJsonObject());
			if(materialTemplate.getCustomEnum()==null || materialTemplate.getCustomEnum().isEmpty()) continue;
			templates.put(materialTemplate.getCustomEnum().toLowerCase(), materialTemplate);
		}
	}

	protected static Collection<String> getCustomEnums(){
		return templates.keySet();
	}
}
