package ch.swisssmp.event.quarantine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.World;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.world.WorldManager;

public class ArenaContainer {
	
	private final World world;
	
	private List<QuarantineArena> arenas = new ArrayList<QuarantineArena>();
	
	ArenaContainer(World world) {
		this.world = world;
	}
	
	public World getWorld() {
		return world;
	}
	
	public Optional<QuarantineArena> getArena(String id) {
		return arenas.stream().filter(a->a.getId().equals(id)).findAny();
	}
	
	public QuarantineArena createArena(String id) {
		Optional<QuarantineArena> existing = getArena(id);
		if(existing.isPresent()) return existing.get();
		QuarantineArena result = new QuarantineArena(this, id);
		arenas.add(result);
		return result;
	}
	
	protected void removeArena(QuarantineArena arena) {
		arenas.remove(arena);
	}
	
	public QuarantineArena[] getArenas() {
		QuarantineArena[] result = new QuarantineArena[arenas.size()];
		arenas.toArray(result);
		return result;
	}
	
	public void save() {
		File containerFile = getContainerFile(world);
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		ConfigurationSection arenasSection = yamlConfiguration.createSection("arenas");
		for(QuarantineArena arena : arenas) {
			ConfigurationSection arenaSection = arenasSection.createSection(arena.getId());
			arena.save(arenaSection);
		}
		yamlConfiguration.save(containerFile);
	}
	
	protected void unload() {
		for(QuarantineArena arena : arenas) {
			QuarantineEventInstance instance = arena.getRunningInstance();
			if(instance==null) continue;
			instance.cancel();
		}
	}
	
	protected static ArenaContainer load(World world) {
		File containerFile = getContainerFile(world);
		if(!containerFile.exists()) return new ArenaContainer(world);
		YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(containerFile);
		if(yamlConfiguration==null) return new ArenaContainer(world);
		return ArenaContainer.load(world, yamlConfiguration);
	}
	
	private static ArenaContainer load(World world, YamlConfiguration yamlConfiguration) {
		ArenaContainer result = new ArenaContainer(world);
		
		ConfigurationSection arenasSection = yamlConfiguration.getConfigurationSection("arenas");
		if(arenasSection!=null) {
			for(String key : arenasSection.getKeys(false)) {
				ConfigurationSection arenaSection = arenasSection.getConfigurationSection(key);
				QuarantineArena arena = QuarantineArena.load(result, arenaSection);
				if(arena==null) {
					continue;
				}
				
				result.arenas.add(arena);
			}
		}
		
		return result;
	}
	
	private static File getContainerFile(World world) {
		File pluginDirectory = WorldManager.getPluginDirectory(QuarantineEventPlugin.getInstance(), world);
		return new File(pluginDirectory, "arena_container.yml");
	}
	
	public static ArenaContainer get(World world) {
		return ArenaContainers.getContainer(world);
	}
}
