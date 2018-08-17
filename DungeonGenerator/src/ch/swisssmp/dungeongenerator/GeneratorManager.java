package ch.swisssmp.dungeongenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class GeneratorManager {
	private static HashMap<World,GeneratorManager> managers = new HashMap<World,GeneratorManager>();
	
	private final HashMap<String,DungeonGenerator> generators = new HashMap<String,DungeonGenerator>();
	private final World world;
	
	private GeneratorManager(World world){
		this.world = world;
	}
	
	protected void unload(){
		for(DungeonGenerator generator : this.generators.values()){
			generator.unload();
		}
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
		YamlConfiguration yamlConfiguration;
		yamlConfiguration = DataSource.getYamlResponse("dungeons/create_generator.php", new String[]{
				"world="+world.getName(),
				"name="+URLEncoder.encode(name),
				"part_size[xz]="+partSizeXZ,
				"part_size[y]="+partSizeY
		});
		if(yamlConfiguration==null || !yamlConfiguration.contains("generator")) return null;
		return new DungeonGenerator(this.world,yamlConfiguration.getConfigurationSection("generator"));
	}
	
	/**
	 * @param name - The name of the DungeonGenerator to be found
	 * @return Returns a DungeonGenerator if found or null
	 */
	public DungeonGenerator get(String name){
		if(!this.generators.containsKey(name.toLowerCase())){
			return this.load(name.toLowerCase());
		}
		else{
			return this.generators.get(name.toLowerCase());
		}
	}
	
	/**
	 * @param name - The name of the DungeonGenerator to be found
	 * @return Returns a DungeonGenerator if found or null
	 */
	public DungeonGenerator get(int generator_id){
		for(DungeonGenerator generator : this.generators.values()){
			if(generator.getId()==generator_id) return generator;
		}
		return this.load(generator_id);
	}
	
	/**
	 * Find out which DungeonGenerator an ItemStack is associated with
	 * @param tokenStack - The ItemStack to check
	 * @return A query with information about whether the ItemStack is associated with any DungeonGenerator and which it is.
	 */
	public DungeonGeneratorQuery get(ItemStack tokenStack){
		net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(tokenStack);
		NBTTagCompound nbtTag = nmsStack.getTag();
		if(nbtTag==null || !nbtTag.hasKey("generator_id")) return new DungeonGeneratorQuery(null, -1, false);
		int generator_id = nbtTag.getInt("generator_id");
		return new DungeonGeneratorQuery(this.get(generator_id), generator_id,true);
	}
	
	/**
	 * @return Returns all DungeonGenerators currently loaded by this manager
	 */
	public Collection<DungeonGenerator> getAll(){
		return this.generators.values();
	}
	
	/**
	 * @param worldName - The name of the Minecraft World to look for
	 * @return Returns a Collection of all DungeonGenerator names which were created in the world with the given worldName
	 */
	public Collection<String> importAll(String worldName){
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("dungeons/get_generators.php", new String[]{
			"world="+URLEncoder.encode(worldName)	
		});
		if(yamlConfiguration==null || !yamlConfiguration.contains("generators")) return new ArrayList<String>();
		return yamlConfiguration.getStringList("generators");
	}
	
	private DungeonGenerator load(String name){
		if(this.generators.containsKey(name.toLowerCase())){
			return this.generators.get(name.toLowerCase());
		}
		return this.load(new String[]{"name="+URLEncoder.encode(name)});
	}
	
	private DungeonGenerator load(int generator_id){
		return this.load(new String[]{"id="+generator_id});
	}
	
	private DungeonGenerator load(String[] args){
		YamlConfiguration yamlConfiguration;
		yamlConfiguration = DataSource.getYamlResponse("dungeons/get_generator.php", args);
		if(yamlConfiguration==null || !yamlConfiguration.contains("generator")) return null;
		return new DungeonGenerator(world,yamlConfiguration.getConfigurationSection("generator"));
	}
	
	public static GeneratorManager get(World world){
		if(managers.containsKey(world)) return managers.get(world);
		GeneratorManager result = new GeneratorManager(world);
		managers.put(world, result);
		return result;
	}
}
