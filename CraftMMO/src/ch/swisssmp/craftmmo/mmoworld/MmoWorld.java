package ch.swisssmp.craftmmo.mmoworld;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ch.swisssmp.craftmmo.Main;
import ch.swisssmp.craftmmo.util.MmoFileUtil;
import ch.swisssmp.craftmmo.util.MmoResourceManager;

public class MmoWorld{
	private static HashMap<Integer, MmoWorld> worlds = new HashMap<Integer, MmoWorld>();
	public static HashMap<String, MmoWorldInstance> instances = new HashMap<String, MmoWorldInstance>();
	private static File worldMapFile;
	public final Integer mmo_world_id;
	public final String name;
	private final World world;
	public final File directory;
	public final RegionManager regionManager;
	
	public MmoWorld(ConfigurationSection dataSection){
		this.mmo_world_id = Integer.parseInt(dataSection.getName());
		this.name = dataSection.getString("name");
		this.world = Bukkit.getWorld(dataSection.getString("mc_world"));
		this.regionManager = Main.worldGuardPlugin.getRegionContainer().get(world);
		this.directory = new File(Main.plugin.getDataFolder(), "worlds/"+world.getName());
		worlds.put(mmo_world_id, this);
	}
	public static MmoWorld get(World world){
		if(world==null) return null;
		MmoWorldInstance worldInstance = instances.get(world.getName());
		if(worldInstance==null) return null;
		int mmo_world_id = worldInstance.mmo_world_id;
		return worlds.get(mmo_world_id);
	}
	public static MmoWorld get(int mmo_world_id){
		return worlds.get(mmo_world_id);
	}
	public static MmoWorld get(Block block){
		World world = block.getWorld();
		return MmoWorld.get(world);
	}
	public static MmoWorldInstance getInstance(String instance_name){
		return instances.get(instance_name);
	}
	public static MmoWorldInstance getInstance(World world){
		if(world==null) return null;
		else return getInstance(world.getName());
	}
	public static MmoWorldInstance getInstance(Block block){
		if(block==null) return null;
		else return getInstance(block.getLocation());
	}
	public static MmoWorldInstance getInstance(Location location){
		if(location==null) return null;
		else return getInstance(location.getWorld());
	}
	public static MmoWorldInstance getInstance(MmoDungeonInstance dungeonInstance){
		if(dungeonInstance==null) return null;
		return getInstance(dungeonInstance.getWorldName());
	}
	public static MmoWorldInstance getInstance(Player player){
		if(player==null) return null;
		return getInstance(player.getWorld());
	}
	public World getWorld(){
		return world;
	}
	public ProtectedRegion getWorldGuardRegion(String name){
		return this.regionManager.getRegion(name);
	}
	public synchronized static void loadWorlds(boolean fullload) throws Exception{
		worlds = new HashMap<Integer, MmoWorld>();
		
		worldMapFile = new File(Main.dataFolder, "worlds.yml");
		instances = new HashMap<String, MmoWorldInstance>();
		if(worldMapFile.exists()){
			YamlConfiguration worldMap = YamlConfiguration.loadConfiguration(worldMapFile);
			for(String key : worldMap.getKeys(false)){
				ConfigurationSection worldSection = worldMap.getConfigurationSection(key);
				MmoWorldInstance.load(worldSection);
			}
		}
		
		YamlConfiguration mmoWorldsConfiguration = MmoResourceManager.getYamlResponse("worlds.php");
		for(String worldIDstring : mmoWorldsConfiguration.getKeys(false)){
			ConfigurationSection dataSection = mmoWorldsConfiguration.getConfigurationSection(worldIDstring);
			String worldName = dataSection.getString("mc_world");
			if(Bukkit.getWorld(worldName)==null)
				continue;
			new MmoWorld(dataSection);
		}
		for(MmoWorld mmoWorld : MmoWorld.worlds.values()){
			if(!instances.containsKey(mmoWorld.world.getName())){
				new MmoWorldInstance(mmoWorld.mmo_world_id, mmoWorld.world.getName(), mmoWorld.world, MmoWorldType.OPEN_WORLD);
			}
		}
	}
	public static void saveWorlds(){
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		for(MmoWorldInstance worldInstance : instances.values()){
			worldInstance.save(yamlConfiguration.createSection(worldInstance.world.getName()));
		}
		try {
			yamlConfiguration.save(worldMapFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void copyDirectory(File source, File target){
        ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.dat"));
		MmoFileUtil.copyDirectory(source, target, ignore);
	}
}
