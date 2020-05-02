package ch.swisssmp.zvierigame;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import org.bukkit.World;

import java.io.File;
import java.util.*;

public class ZvieriArenen {
	
	private static HashMap<UUID, ZvieriArena> loadedArenas = new HashMap<UUID, ZvieriArena>();
	
	public static Collection<ZvieriArena> getAll(){
		return loadedArenas.values();
	}
	
	public static Collection<ZvieriArena> get(World world){
		List<ZvieriArena> result = new ArrayList<ZvieriArena>();
		for(ZvieriArena arena : loadedArenas.values()) {
			if(arena.getWorld() == world) {
				result.add(arena);
			}
		}
		return result;
	}
	
	public static void save(World world) {
		File pluginDirectory = new File(world.getWorldFolder(), "plugindata/ZvieriGame");
		File dataFile = new File(pluginDirectory, "arenen.yml");
		
		if(!pluginDirectory.exists()) {
			pluginDirectory.mkdirs();
		}
		
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		ConfigurationSection arenenSection = yamlConfiguration.createSection("arenen");
		
		int index = 1;
		for(ZvieriArena arena : ZvieriArenen.get(world)) {
			ConfigurationSection arenaSection = arenenSection.createSection("arena_" + index);
			arena.save(arenaSection);
			index++;
		}
		yamlConfiguration.save(dataFile);
	}
	
	public static void load(World world) {
		unload(world);
		File dataFile = new File(world.getWorldFolder(), "plugindata/ZvieriGame/arenen.yml");
		if(dataFile.exists()) {
			ZvieriArenen.load(world, dataFile);
		} else{ }
	}

	public static void load(World world, File dataFile) {		
		YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(dataFile);
		if(yamlConfiguration.contains("arenen")) {
			ConfigurationSection arenenSection = yamlConfiguration.getConfigurationSection("arenen");
			for(String key : arenenSection.getKeys(false)) {
				if(arenenSection.getKeys(false).contains(key)) {
					ConfigurationSection arenaSection = arenenSection.getConfigurationSection(key);
					ZvieriArena arena = ZvieriArena.load(world, arenaSection);
					loadedArenas.put(arena.getId(), arena);
				}
			}
		}	
	}
	
	public static void unload(World world) {
		for(ZvieriArena arena : ZvieriArenen.get(world)) {
			remove(arena.getId());
		}
	}
	
	protected static boolean containsKey(UUID arena_id) {
		return loadedArenas.containsKey(arena_id);
	}
	
	protected static ZvieriArena get(UUID arena_id) {
		return loadedArenas.get(arena_id);
	}
	
	protected static void remove(UUID arena_id) {
		loadedArenas.remove(arena_id);
	}
	
	protected static void put(UUID arena_id, ZvieriArena arena) {
		loadedArenas.put(arena_id, arena);
	}
}
