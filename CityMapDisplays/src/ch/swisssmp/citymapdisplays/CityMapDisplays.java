package ch.swisssmp.citymapdisplays;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;

public class CityMapDisplays {
	private static List<CityMapDisplay> displays = new ArrayList<CityMapDisplay>();
	
	protected static void add(CityMapDisplay display) {
		displays.add(display);
	}
	
	protected static void remove(CityMapDisplay display) {
		displays.remove(display);
	}
	
	protected static Optional<CityMapDisplay> get(UUID displayUid){
		return displays.stream().filter(d->d.getUid().equals(displayUid)).findAny();
	}
	
	protected static void save() {
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		ConfigurationSection displaysSection = yamlConfiguration.createSection("displays");
		for(CityMapDisplay display : displays) {
			ConfigurationSection displaySection = displaysSection.createSection(display.getUid().toString());
			display.save(displaySection);
		}
		
		File file = getFile();
		if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
		yamlConfiguration.save(file);
	}
	
	protected static void load() {
		displays.clear();
		File file = getFile();
		if(!file.exists()) {
			Bukkit.getLogger().info(CityMapDisplaysPlugin.getPrefix()+" Cannot load existing CityMapDisplays because there is no save file.");
			return;
		}
		YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
		if(yamlConfiguration==null) {
			Bukkit.getLogger().info(CityMapDisplaysPlugin.getPrefix()+" Cannot load existing CityMapDisplays because the save file is invalid.");
			return;
		}
		ConfigurationSection displaysSection = yamlConfiguration.getConfigurationSection("displays");
		if(displaysSection!=null) {
			for(String key : displaysSection.getKeys(false)) {
				UUID displayUid;
				try {
					displayUid = UUID.fromString(key);
				}
				catch(Exception e) {
					continue;
				}
				ConfigurationSection displaySection = displaysSection.getConfigurationSection(key);
				CityMapDisplay display = CityMapDisplay.load(displayUid, displaySection);
				displays.add(display);
			}
		}
		Bukkit.getLogger().info("Loaded "+displays.size()+" displays.");
	}
	
	protected static void unload() {
		for(CityMapDisplay display : displays) {
			display.unload();
		}
		displays.clear();
	}
	
	protected static File getFile() {
		return new File(CityMapDisplaysPlugin.getInstance().getDataFolder(), "displays.yml");
	}
}
