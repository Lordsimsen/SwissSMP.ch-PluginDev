package ch.swisssmp.world;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.FileUtil;

public class WorldLoader {
	protected static World load(String worldName, ConfigurationSection dataSection){
		//Load World Border
		WorldBorder worldBorder = null;
		if(dataSection.contains("world_border")){
			if(WorldManager.worldBorders.containsKey(worldName)) WorldManager.worldBorders.remove(worldName);
			worldBorder = WorldBorder.create(dataSection.getConfigurationSection("world_border"));
			WorldManager.worldBorders.put(worldName, worldBorder);
		}
		//Load Bukkit World
		World existing = Bukkit.getWorld(worldName);
		if(existing!=null){
			WorldLoader.applyWorldBorder(existing, worldBorder);
			return existing;
		}
		//Copy Advancements
		WorldLoader.copyDefaultAdvancements(worldName);
		//Make World Creator
		WorldCreator creator = WorldLoader.getWorldCreator(worldName, dataSection);
		//Create World
		World result = Bukkit.createWorld(creator);
		if(result==null) return null;
		//Set time
		result.setTime(dataSection.contains("time") ? dataSection.getLong("time") : 0);
		//Apply Game Rules
		if(dataSection.contains("gamerules")){
			WorldLoader.applyGameRules(result, dataSection.getConfigurationSection("gamerules"));
		}
		//Set Spawn
		result.setSpawnLocation(dataSection.getInt("spawn_x"), dataSection.getInt("spawn_y"), dataSection.getInt("spawn_z"));
		//Apply World Border
		WorldLoader.applyWorldBorder(result, worldBorder);
		return result;
	}
	
	private static void applyWorldBorder(World world, WorldBorder worldBorder){
		//Apply World Border
		if(worldBorder!=null && !worldBorder.doWrap()){
			world.getWorldBorder().setCenter(worldBorder.getCenterX(), worldBorder.getCenterZ());
			world.getWorldBorder().setSize(worldBorder.getRadius()*2);
			world.getWorldBorder().setWarningDistance(worldBorder.getMargin());
		}
		else if(worldBorder!=null && worldBorder.doWrap()){
			world.getWorldBorder().reset();
		}
	}
	
	private static WorldCreator getWorldCreator(String worldName, ConfigurationSection dataSection){
		WorldCreator result = new WorldCreator(worldName);
		result.environment(Environment.valueOf(dataSection.getString("environment")));
		result.generateStructures(dataSection.getBoolean("generate_structures"));
		result.seed(dataSection.getLong("seed"));
		result.type(WorldType.valueOf(dataSection.getString("world_type")));
		return result;
	}
	
	/**
	 * Copies all advancements from the main World to the target World. This is necessary to maintain the advancement system.
	 * @param worldName - The target World to copy the advancements to
	 */
	private static void copyDefaultAdvancements(String worldName){
		File mainWorldAdvancementsFile = new File(Bukkit.getWorldContainer(), Bukkit.getWorlds().get(0).getName()+"/data/advancements");
		File worldAdvancementsFile = new File(Bukkit.getWorldContainer(), worldName+"/data/advancements");
		if(worldAdvancementsFile.exists()){
			FileUtil.deleteRecursive(worldAdvancementsFile);
		}
		FileUtil.copyDirectory(mainWorldAdvancementsFile, worldAdvancementsFile);
	}
	
	/**
	 * Applies all gamerules from the gamerulesSection to the given World
	 * @param world - The world to apply the gamerules to
	 * @param gamerulesSection - A ConfigurationSection with gamerules and associated values
	 */
	private static void applyGameRules(World world, ConfigurationSection gamerulesSection){
		for(String gamerule : gamerulesSection.getKeys(false)){
			if(!world.setGameRuleValue(gamerule, gamerulesSection.getString(gamerule))){
				Bukkit.getLogger().info("[WorldManager] Gamerule "+gamerule+" für Welt "+world.getName()+" konnte nicht auf "+gamerulesSection.getString(gamerule)+" gesetzt werden.");
			}
		}
	}
}
