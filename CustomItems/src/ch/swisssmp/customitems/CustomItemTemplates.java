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

public class CustomItemTemplates {
	protected static HashMap<String,CustomItemTemplate> templates = new HashMap<String,CustomItemTemplate>();
	
	protected static void load(){
		HTTPRequest request = DataSource.getResponse(CustomItemsPlugin.getInstance(), "get_items.php", RequestMethod.POST_SYNC);
		load(request.getJsonResponse());
	}
	
	protected static void load(JsonObject json){
		templates.clear();
		if(json==null || !json.has("items")) return;
		JsonArray itemsArray = json.getAsJsonArray("items");
		for(JsonElement element : itemsArray){
			if(!element.isJsonObject()) continue;
			CustomItemTemplate itemTemplate = new CustomItemTemplate(element.getAsJsonObject());
			if(itemTemplate.getCustomEnum()==null || itemTemplate.getCustomEnum().isEmpty()) continue;
			templates.put(itemTemplate.getCustomEnum().toLowerCase(), itemTemplate);
		}
	}

	protected static Collection<String> getCustomEnums(){
		return templates.keySet();
	}
}
