package ch.swisssmp.addonabnahme;

import java.util.HashMap;

import ch.swisssmp.utils.ConfigurationSection;

public class Techtree {

	private final String techtree_id;
	private final HashMap<String,AddonInfo> addons = new HashMap<String,AddonInfo>();
	
	protected Techtree(ConfigurationSection dataSection){
		this.techtree_id = dataSection.getString("techtree_id");
		ConfigurationSection addonsSection = dataSection.getConfigurationSection("addons");
		if(addonsSection!=null){
			for(String key : addonsSection.getKeys(false)){
				ConfigurationSection addonSection = addonsSection.getConfigurationSection(key);
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
	
	public static Techtree get(String techtree_id){
		return Techtrees.getTechtree(techtree_id);
	}
}
