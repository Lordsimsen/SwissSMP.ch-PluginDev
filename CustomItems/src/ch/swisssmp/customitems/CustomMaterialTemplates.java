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

public class CustomMaterialTemplates {
	private static final Set<CustomMaterialTemplate> templates = new HashSet<>();

	protected static Optional<CustomMaterialTemplate> getTemplate(NamespacedKey key){
		return templates.stream().filter(t->t.getKey().equals(key)).findAny();
	}

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
			CustomMaterialTemplate materialTemplate = CustomMaterialTemplate.load(element.getAsJsonObject()).orElse(null);
			if(materialTemplate==null) continue;
			templates.add(materialTemplate);
		}
	}

	protected static Collection<NamespacedKey> getKeys(){
		return templates.stream().map(CustomMaterialTemplate::getKey).collect(Collectors.toList());
	}
}
