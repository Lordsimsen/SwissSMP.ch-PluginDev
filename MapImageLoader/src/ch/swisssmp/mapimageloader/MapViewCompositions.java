package ch.swisssmp.mapimageloader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class MapViewCompositions {
	private static final List<MapViewComposition> compositions = new ArrayList<MapViewComposition>();
	
	protected static Optional<MapViewComposition> get(int mapId) {
		return compositions.stream().filter(c->c.getMapId()==mapId).findAny();
	}
	
	protected static Iterable<MapViewComposition> get(MapImage image){
		return compositions.stream().filter(c->c.contains(image)).collect(Collectors.toList());
	}
	
	protected static void add(MapViewComposition c) {
		compositions.add(c);
	}
	
	protected static void remove(MapViewComposition c) {
		compositions.remove(c);
	}
	
	/**
	 * Saves all current MapViewCompositions
	 */
	public static void save() {
		File file = getFile();
		if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		ConfigurationSection compositionsSection = yamlConfiguration.createSection("compositions");
		for(MapViewComposition c : compositions) {
			ConfigurationSection compositionSection = compositionsSection.createSection("map_"+c.getMapId());
			c.save(compositionSection);
		}
		try {
			yamlConfiguration.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads all saved MapViewCompositions
	 */
	protected static void load() {
		compositions.clear();
		File file = getFile();
		if(!file.exists()) {
			Bukkit.getLogger().info(MapImageLoaderPlugin.getPrefix()+" Cannot load compositions because the save file is missing.");
			return;
		}
		YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
		if(yamlConfiguration==null) {
			Bukkit.getLogger().info(MapImageLoaderPlugin.getPrefix()+" Cannot load compositions because the save file is invalid.");
			return;
		}
		ConfigurationSection compositionsSection = yamlConfiguration.getConfigurationSection("compositions");
		if(compositionsSection!=null) {
			for(String key : compositionsSection.getKeys(false)) {
				ConfigurationSection compositionSection = compositionsSection.getConfigurationSection(key);
				MapViewComposition c = MapViewComposition.load(compositionSection);
				if(c==null) continue;
				compositions.add(c);
			}
		}
		Bukkit.getLogger().info(MapImageLoaderPlugin.getPrefix()+" Loaded "+compositions.size()+" compositions.");
	}
	
	public static void unload() {
		for(MapViewComposition c : compositions) {
			c.unload();
		}
	}
	
	private static File getFile() {
		return new File(MapImageLoaderPlugin.getInstance().getDataFolder(), "map_view_compositions.yml");
	}
}
