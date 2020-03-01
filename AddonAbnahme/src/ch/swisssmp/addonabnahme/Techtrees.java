package ch.swisssmp.addonabnahme;

import java.util.HashMap;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;

public class Techtrees {
	
	private static HashMap<String,Techtree> techtrees = new HashMap<String,Techtree>();
	
	protected static Techtree getTechtree(String techtree_id){
		return techtrees.get(techtree_id);
	}
	
	protected static void loadAll(){
		HTTPRequest request = DataSource.getResponse(AddonAbnahme.getInstance(), "load_techtrees.php");
		request.onFinish(()->{
			loadAll(request.getYamlResponse());
		});
	}
	
	private static void loadAll(YamlConfiguration yamlConfiguration){
		if(yamlConfiguration==null || !yamlConfiguration.contains("techtrees")) return;
		techtrees.clear();
		ConfigurationSection techtreesSection = yamlConfiguration.getConfigurationSection("techtrees");
		for(String key : techtreesSection.getKeys(false)){
			ConfigurationSection techtreeSection = techtreesSection.getConfigurationSection(key);
			Techtree techtree = new Techtree(techtreeSection);
			techtree.loadIcons();
			techtrees.put(techtree.getTechtreeId(), techtree);
		}
	}
}
