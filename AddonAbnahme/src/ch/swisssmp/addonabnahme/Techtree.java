package ch.swisssmp.addonabnahme;

import java.util.HashMap;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Techtree {

	private final String techtree_id;
	private final HashMap<String,AddonInfo> addons = new HashMap<String,AddonInfo>();
	
	protected Techtree(JsonObject json){
		this.techtree_id = JsonUtil.getString("techtree_id", json);
		JsonArray addonsSection = json.has("addons") ? json.getAsJsonArray("addons") : null;
		if(addonsSection!=null){
			for(JsonElement element : addonsSection){
				if(!element.isJsonObject()) continue;
				JsonObject addonSection = element.getAsJsonObject();
				AddonInfo addonInfo = new AddonInfo(addonSection);
				addons.put(addonInfo.getAddonId(), addonInfo);
			}
		}
	}
	
	public String getTechtreeId(){
		return this.techtree_id;
	}
	
	public AddonInfo getAddon(String key){
		if(addons.containsKey(key)) return addons.get(key);
		for(AddonInfo addonInfo : addons.values()){
			if(addonInfo.getName().toLowerCase().contains(key.toLowerCase())) return addonInfo;
			for(String synonym : addonInfo.getSynonyms()){
				if(synonym.toLowerCase().contains(key.toLowerCase())) return addonInfo;
			}
		}
		return null;
	}
	
	public void loadIcons(){
		for(AddonInfo addonInfo : addons.values()){
			LivemapInterface.updateAddonIcon(addonInfo);
		}
	}
	
	public static Techtree get(String techtree_id){
		return Techtrees.getTechtree(techtree_id);
	}
}
