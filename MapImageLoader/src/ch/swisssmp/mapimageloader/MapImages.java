package ch.swisssmp.mapimageloader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class MapImages {
	private static Ditherer ditherer;
	private final static List<MapImage> images = new ArrayList<MapImage>();
	
	public static Iterable<MapImage> getAll(){
		return images;
	}
	
	public static Ditherer getDitherer() {
		return ditherer;
	}
	
	protected static Optional<MapImage> get(UUID uid) {
		return images.stream().filter(v->v.getUid().equals(uid)).findAny();
	}
	
	protected static Optional<MapImage> getByUrl(String url) {
		return images.stream().filter(v->v.getUrl().equals(url)).findAny();
	}
	
	protected static void add(MapImage image) {
		images.add(image);
	}
	
	protected static void remove(MapImage image) {
		images.remove(image);
	}
	
	/**
	 * Saves all current MapImages
	 */
	public static void save() {
		File file = getFile();
		if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		ConfigurationSection imagesSection = yamlConfiguration.createSection("images");
		for(MapImage image : images) {
			ConfigurationSection imageSection = imagesSection.createSection(image.getUid().toString());
			image.save(imageSection);
		}
		try {
			yamlConfiguration.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads all saved MapImages
	 */
	protected static void load(Ditherer ditherer) {
		MapImages.ditherer = ditherer;
		File file = getFile();
		if(!file.exists()) return;
		images.clear();
		YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
		ConfigurationSection imagesSection = yamlConfiguration.getConfigurationSection("images");
		if(imagesSection!=null) {
			for(String key : imagesSection.getKeys(false)) {
				try {
					UUID uid = UUID.fromString(key);
					ConfigurationSection imageSection = imagesSection.getConfigurationSection(key);
					MapImage.load(uid, imageSection);
				}
				catch(Exception e) {
					continue;
				}
			}
		}
	}
	
	/**
	 * Unloads all MapImages and frees their BufferedImage memory
	 */
	protected static void unload() {
		for(MapImage i : images) {
			i.unload();
		}
		images.clear();
	}
	
	private static File getFile() {
		return new File(MapImageLoaderPlugin.getInstance().getDataFolder(), "map_images.yml");
	}
}
