package ch.swisssmp.ageofempires;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

import org.bukkit.Bukkit;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;

public class TauntEntries {
	private static HashMap<String,TauntEntry> entries = new HashMap<String,TauntEntry>();
	
	protected static void add(TauntEntry entry) {
		entries.put(entry.getKey(), entry);
	}
	
	protected static void remove(TauntEntry entry) {
		entries.remove(entry.getKey());
	}
	
	protected static Optional<TauntEntry> get(String key){
		return entries.containsKey(key) ? Optional.of(entries.get(key)) : Optional.empty();
	}
	
	public static Collection<TauntEntry> getAll(){
		return entries.values();
	}
	
	public static void reload() {
		HTTPRequest request = DataSource.getResponse(AgeOfEmpiresTauntsPlugin.getInstance(), "get_taunts.php");
		request.onFinish(()->{
			YamlConfiguration yamlConfiguration = request.getYamlResponse();
			if(yamlConfiguration==null || !yamlConfiguration.contains("taunts")) {
				Bukkit.getLogger().info(AgeOfEmpiresTauntsPlugin.getPrefix()+" Fehler beim Laden der Taunts. Bitte API prüfen.");
				return;
			}
			
			reload(yamlConfiguration);
		});
	}
	
	private static void reload(YamlConfiguration yamlConfiguration) {
		entries.clear();
		ConfigurationSection tauntsSection = yamlConfiguration.getConfigurationSection("taunts");
		if(tauntsSection==null) {
			return;
		}
		
		int count = 0;
		for(String key : tauntsSection.getKeys(false)) {
			ConfigurationSection tauntSection = tauntsSection.getConfigurationSection(key);
			TauntEntry taunt = TauntEntry.get(tauntSection);
			if(taunt==null) {
				continue;
			}
			
			add(taunt);
			count++;
		}
		
		Bukkit.getLogger().info(AgeOfEmpiresTauntsPlugin.getPrefix()+" "+count+" Einträge geladen.");
	}
}
