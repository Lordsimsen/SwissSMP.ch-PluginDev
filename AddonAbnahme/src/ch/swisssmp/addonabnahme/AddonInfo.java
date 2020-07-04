package ch.swisssmp.addonabnahme;

import java.util.Collection;
import java.util.HashSet;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonObject;

public class AddonInfo {
	
	private final String addon_id;
	private final String name;
	private final String livemapIconUrl;
	private final HashSet<String> synonyms = new HashSet<String>();
	private final int cityLevel;
	
	protected AddonInfo(JsonObject json){
		this.addon_id = JsonUtil.getString("addon_id", json);
		this.name = JsonUtil.getString("name", json);
		this.livemapIconUrl = JsonUtil.getString("icon", json);
		if(json.has("synonyms")){
			this.synonyms.addAll(JsonUtil.getStringList("synonyms", json));
		}
		this.cityLevel = JsonUtil.getInt("level", json);
	}
	
	public String getAddonId(){
		return addon_id;
	}
	
	public String getIconId(){
		return "addon_"+addon_id;
	}
	
	public String getLivemapIconUrl(){
		return livemapIconUrl;
	}
	
	public String getName(){
		return name;
	}
	
	public int getCityLevel(){
		return this.cityLevel;
	}
	
	public Collection<String> getSynonyms(){
		return this.synonyms;
	}
}
