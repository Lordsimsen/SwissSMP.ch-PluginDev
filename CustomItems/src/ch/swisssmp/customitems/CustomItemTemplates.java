package ch.swisssmp.customitems;

import java.util.*;
import java.util.stream.Collectors;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import ch.swisssmp.webcore.RequestMethod;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;

public class CustomItemTemplates {
	private static final Set<CustomItemTemplate> templates = new HashSet<>();

	protected static Optional<CustomItemTemplate> getTemplate(NamespacedKey key){
		return templates.stream().filter(t->t.getKey().equals(key)).findAny();
	}

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
			CustomItemTemplate itemTemplate = CustomItemTemplate.load(element.getAsJsonObject()).orElse(null);
			if(itemTemplate.getKey()==null) continue;
			templates.add(itemTemplate);
		}
	}

	protected static Collection<NamespacedKey> getKeys(){
		return templates.stream().map(CustomItemTemplate::getKey).collect(Collectors.toList());
	}
}
