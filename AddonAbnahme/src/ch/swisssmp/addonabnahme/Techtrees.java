package ch.swisssmp.addonabnahme;

import java.util.HashMap;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Techtrees {
	
	private static HashMap<String,Techtree> techtrees = new HashMap<String,Techtree>();
	
	protected static Techtree getTechtree(String techtree_id){
		return techtrees.get(techtree_id);
	}
	
	protected static void loadAll(){
		HTTPRequest request = DataSource.getResponse(AddonAbnahme.getInstance(), "load_techtrees.php");
		request.onFinish(()->{
			loadAll(request.getJsonResponse());
		});
	}
	
	private static void loadAll(JsonObject json){
		if(json==null || !json.has("techtrees")) return;
		techtrees.clear();
		JsonArray techtreesSection = json.getAsJsonArray("techtrees");
		for(JsonElement element : techtreesSection){
			if(!element.isJsonObject()) continue;
			JsonObject techtreeSection = element.getAsJsonObject();
			Techtree techtree = new Techtree(techtreeSection);
			techtree.loadIcons();
			techtrees.put(techtree.getTechtreeId(), techtree);
		}
	}
}
