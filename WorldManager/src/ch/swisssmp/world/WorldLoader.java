package ch.swisssmp.world;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.FileUtil;
import ch.swisssmp.world.border.WorldBorder;
import ch.swisssmp.world.border.WorldBorderManager;

public class WorldLoader {
	protected static World load(String worldName, ConfigurationSection dataSection){
		//Clear old World Border
		WorldBorderManager.removeWorldBorder(worldName);
		//Load Bukkit World
		World result = Bukkit.getWorld(worldName);
		if(result==null){
			result = WorldLoader.createWorld(worldName, dataSection);
		}
		if(dataSection.contains("world_border")){
			//Load World Border
			WorldBorder worldBorder = WorldBorder.create(dataSection.getConfigurationSection("world_border"));
			//Apply World Border
			WorldBorderManager.setWorldBorder(worldName, worldBorder);
		}
		return result;
	}
	
	private static World createWorld(String worldName, ConfigurationSection dataSection){
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
		
		return result;
	}
	
	private static WorldCreator getWorldCreator(String worldName, ConfigurationSection dataSection){
		WorldCreator result = new WorldCreator(worldName);
		result.environment(Environment.valueOf(dataSection.getString("environment")));
		result.generateStructures(dataSection.getBoolean("generate_structures"));
		result.seed(dataSection.getLong("seed"));
		WorldType worldType = WorldType.valueOf(dataSection.getString("world_type"));
		result.type(worldType);
		return result;
	}
	
	/**
	 * Copies all advancements from the main World to the target World. This is necessary to maintain the advancement system.
	 * @param worldName - The target World to copy the advancements to
	 */
	private static void copyDefaultAdvancements(String worldName){
		File mainWorldAdvancementsFile = new File(Bukkit.getWorldContainer(), Bukkit.getWorlds().get(0).getName()+"/data/advancements");
		if(!mainWorldAdvancementsFile.exists()) return;
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
	@SuppressWarnings("deprecation")
	private static void applyGameRules(World world, ConfigurationSection gamerulesSection){
		for(String gameruleName : gamerulesSection.getKeys(false)){
			String value = gamerulesSection.getString(gameruleName);
			if(!world.setGameRuleValue(gameruleName, value)){
				Bukkit.getLogger().info("[WorldManager] Gamerule "+gameruleName+" für Welt "+world.getName()+" konnte nicht auf "+value+" gesetzt werden.");
			}
		}
	}
}
