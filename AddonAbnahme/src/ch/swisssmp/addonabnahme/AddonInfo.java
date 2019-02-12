package ch.swisssmp.addonabnahme;

import java.util.Collection;
import java.util.HashSet;

import ch.swisssmp.utils.ConfigurationSection;

public class AddonInfo {
	
	private final String addon_id;
	private final String name;
	private final HashSet<String> synonyms = new HashSet<String>();
	private final int cityLevel;
	
	protected AddonInfo(ConfigurationSection dataSection){
		this.addon_id = dataSection.getString("addon_id");
		this.name = dataSection.getString("name");
		if(dataSection.contains("synonyms")){
			this.synonyms.addAll(dataSection.getStringList("synonyms"));
		}
		this.cityLevel = dataSection.getInt("level");
	}
	
	public String getAddonId(){
		return addon_id;
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
