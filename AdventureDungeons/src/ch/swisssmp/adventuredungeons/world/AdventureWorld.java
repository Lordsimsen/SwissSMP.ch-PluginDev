package ch.swisssmp.adventuredungeons.world;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ch.swisssmp.adventuredungeons.AdventureDungeons;
import ch.swisssmp.adventuredungeons.util.MmoFileUtil;
import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class AdventureWorld{
	private static HashMap<Integer, AdventureWorld> worlds = new HashMap<Integer, AdventureWorld>();
	public static HashMap<String, AdventureWorldInstance> instances = new HashMap<String, AdventureWorldInstance>();
	private static File worldMapFile;
	public final Integer world_id;
	public final String name;
	private final World world;
	public final RegionManager regionManager;
	
	public AdventureWorld(ConfigurationSection dataSection){
		this.world_id = Integer.parseInt(dataSection.getName());
		this.name = dataSection.getString("name");
		this.world = Bukkit.getWorld(dataSection.getString("mc_world"));
		this.regionManager = AdventureDungeons.worldGuardPlugin.getRegionContainer().get(world);
		worlds.put(world_id, this);
	}
	public static AdventureWorld get(World world){
		if(world==null) return null;
		AdventureWorldInstance worldInstance = instances.get(world.getName());
		if(worldInstance==null) return null;
		int mmo_world_id = worldInstance.world_id;
		return worlds.get(mmo_world_id);
	}
	public static AdventureWorld get(int mmo_world_id){
		return worlds.get(mmo_world_id);
	}
	public static AdventureWorld get(Block block){
		World world = block.getWorld();
		return AdventureWorld.get(world);
	}
	public static AdventureWorldInstance getInstance(String instance_name){
		return instances.get(instance_name);
	}
	public static AdventureWorldInstance getInstance(World world){
		if(world==null) return null;
		else return getInstance(world.getName());
	}
	public static AdventureWorldInstance getInstance(Block block){
		if(block==null) return null;
		else return getInstance(block.getLocation());
	}
	public static AdventureWorldInstance getInstance(Location location){
		if(location==null) return null;
		else return getInstance(location.getWorld());
	}
	public static AdventureWorldInstance getInstance(DungeonInstance dungeonInstance){
		if(dungeonInstance==null) return null;
		return getInstance(dungeonInstance.getWorldName());
	}
	public static AdventureWorldInstance getInstance(Player player){
		if(player==null) return null;
		return getInstance(player.getWorld());
	}
	public World getWorld(){
		return world;
	}
	public ProtectedRegion getWorldGuardRegion(String name){
		return this.regionManager.getRegion(name);
	}
	public static void loadWorlds(boolean fullload) throws Exception{
		worlds = new HashMap<Integer, AdventureWorld>();
		
		worldMapFile = new File(AdventureDungeons.dataFolder, "worlds.yml");
		instances = new HashMap<String, AdventureWorldInstance>();
		if(worldMapFile.exists()){
			YamlConfiguration worldMap = YamlConfiguration.loadConfiguration(worldMapFile);
			for(String key : worldMap.getKeys(false)){
				ConfigurationSection worldSection = worldMap.getConfigurationSection(key);
				AdventureWorldInstance.load(worldSection);
			}
		}
		
		YamlConfiguration mmoWorldsConfiguration = DataSource.getYamlResponse("adventure/worlds.php");
		for(String worldIDstring : mmoWorldsConfiguration.getKeys(false)){
			ConfigurationSection dataSection = mmoWorldsConfiguration.getConfigurationSection(worldIDstring);
			String worldName = dataSection.getString("mc_world");
			if(Bukkit.getWorld(worldName)==null)
				continue;
			new AdventureWorld(dataSection);
		}
		for(AdventureWorld mmoWorld : AdventureWorld.worlds.values()){
			if(!instances.containsKey(mmoWorld.world.getName())){
				new AdventureWorldInstance(mmoWorld.world_id, mmoWorld.world.getName(), mmoWorld.world, AdventureWorldType.OPEN_WORLD);
			}
		}
	}
	public static void saveWorlds(){
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		for(AdventureWorldInstance worldInstance : instances.values()){
			worldInstance.save(yamlConfiguration.createSection(worldInstance.world.getName()));
		}
		yamlConfiguration.save(worldMapFile);
	}
	public static void copyDirectory(File source, File target){
        ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.dat"));
		MmoFileUtil.copyDirectory(source, target, ignore);
	}
}
