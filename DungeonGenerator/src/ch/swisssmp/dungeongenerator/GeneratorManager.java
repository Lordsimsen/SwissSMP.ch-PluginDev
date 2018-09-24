package ch.swisssmp.dungeongenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.World;
import org.bukkit.entity.Player;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;

public class GeneratorManager {
	private static HashMap<World,GeneratorManager> managers = new HashMap<World,GeneratorManager>();

	private final World world;
	private final HashMap<Integer,DungeonGenerator> generators = new HashMap<Integer,DungeonGenerator>();
	private final HashMap<String,DungeonGenerator> browserInspections = new HashMap<String,DungeonGenerator>();
	
	private GeneratorManager(World world){
		this.world = world;
	}
	
	/**
	 * Creates a new DungeonGenerator in the database with the settings provided
	 * @param name - The name of the new DungeonGenerator
	 * @param partSizeXZ - The size of the parts on the XZ plane
	 * @param partSizeY - The size of the parts on the Y axis
	 * @return Returns a newly created DungeonGenerator
	 */
	public DungeonGenerator create(String name, int partSizeXZ, int partSizeY){
		DungeonGenerator existing = this.get(name.toLowerCase());
		if(existing!=null) return existing;
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		ConfigurationSection dataSection = yamlConfiguration.createSection("generator");
		int generator_id = this.generators.size()>0 ? Collections.max(this.generators.keySet())+1 : 1;
		dataSection.set("generator_id", generator_id);
		dataSection.set("generator_name", name);
		dataSection.set("part_size_xz", partSizeXZ);
		dataSection.set("part_size_y", partSizeY);
		
		DungeonGenerator result = new DungeonGenerator(this,yamlConfiguration.getConfigurationSection("generator"));
		this.generators.put(generator_id, result);
		return result;
	}
	
	/**
	 * @param generator_id - The ID of the DungeonGenerator to be found
	 * @return A DungeonGenerator if found;
	 * 		   <code>null</code> otherwise
	 */
	public DungeonGenerator get(int generator_id){
		return this.generators.get(generator_id);
	}
	
	/**
	 * @param name - The name of the DungeonGenerator to be found
	 * @return A DungeonGenerator if found;
	 * 		   <code>null</code> otherwise
	 */
	public DungeonGenerator get(String name){
		for(DungeonGenerator generator : this.generators.values()){
			if(generator.getName().toLowerCase().contains(name.toLowerCase())) return generator;
		}
		return null;
	}
	
	/**
	 * @return Returns all DungeonGenerators currently loaded by this manager
	 */
	public Collection<DungeonGenerator> getAll(){
		return this.generators.values();
	}
	
	public World getWorld(){
		return this.world;
	}
	
	protected void addBrowserInspection(Player player, DungeonGenerator generator){
		this.browserInspections.put(player.getName(), generator);
	}
	
	protected void stopBrowserInspection(Player player){
		this.browserInspections.remove(player.getName());
	}
	
	protected Collection<String> getInspectors(DungeonGenerator generator){
		Collection<String> result = new ArrayList<String>();
		for(Entry<String,DungeonGenerator> entry : this.browserInspections.entrySet()){
			if(entry.getValue()!=generator) continue;
			result.add(entry.getKey());
		}
		return result;
	}
	
	protected void saveAll(){
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		ConfigurationSection generatorsSection = yamlConfiguration.createSection("generators");
		ConfigurationSection generatorSection;
		for(DungeonGenerator dungeonGenerator : this.generators.values()){
			generatorSection = generatorsSection.createSection("generator_"+dungeonGenerator.getId());
			dungeonGenerator.save(generatorSection);
		}
		File file = this.getGeneratorsFile();
		if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
		yamlConfiguration.save(file);
	}
	
	protected void unload(){
		for(DungeonGenerator generator : this.generators.values()){
			generator.unload();
		}
	}
	
	private File getGeneratorsFile(){
		return new File(this.world.getWorldFolder(), "plugindata/dungeon_generators.yml");
	}
	
	private void importAll(){
		File generatorsFile = this.getGeneratorsFile();
		if(!generatorsFile.exists()) return;
		YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(generatorsFile);
		if(yamlConfiguration==null || !yamlConfiguration.contains("generators")) return;
		ConfigurationSection generatorsSection = yamlConfiguration.getConfigurationSection("generators");
		ConfigurationSection generatorSection;
		int generator_id;
		for(String key : generatorsSection.getKeys(false)){
			generatorSection = generatorsSection.getConfigurationSection(key);
			generator_id = generatorSection.getInt("generator_id");
			this.generators.put(generator_id, new DungeonGenerator(this, generatorSection));
		}
	}
	public static GeneratorManager get(World world){
		if(managers.containsKey(world)) return managers.get(world);
		GeneratorManager result = new GeneratorManager(world);
		result.importAll();
		managers.put(world, result);
		return result;
	}
}
