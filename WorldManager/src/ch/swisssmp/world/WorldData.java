package ch.swisssmp.world;

import java.io.File;

import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.world.border.WorldBorder;
import ch.swisssmp.world.border.WorldBorderManager;

class WorldData {
	
	private World world;
	private YamlConfiguration worldSettings;
	
	public WorldData(World world){
		this.world = world;
	}
	
	World getWorld(){
		return world;
	}
	
	YamlConfiguration getSettings(){
		return worldSettings;
	}
	
	void loadSettings(){
		File localSettingsFile = WorldManager.getSettingsFile(world.getName());
		YamlConfiguration yamlConfiguration = null;
		if(localSettingsFile.exists()){
			yamlConfiguration = YamlConfiguration.loadConfiguration(localSettingsFile);
		}
		if(yamlConfiguration==null){
			yamlConfiguration = new YamlConfiguration();
		}
		if(!yamlConfiguration.contains("world")){
			yamlConfiguration.createSection("world");
		}
		worldSettings = yamlConfiguration;
	}
	
	void saveSettings(){
		int spawnRadius = world.getGameRuleValue(GameRule.SPAWN_RADIUS);
		world.setGameRule(GameRule.SPAWN_RADIUS, 0);
		Location spawnLocation = world.getSpawnLocation();
		world.setGameRule(GameRule.SPAWN_RADIUS, spawnRadius);
		YamlConfiguration yamlConfiguration = worldSettings;
		ConfigurationSection dataSection = yamlConfiguration.getConfigurationSection("world");
		dataSection.set("environment", world.getEnvironment().toString());
		dataSection.set("generate_structures", world.canGenerateStructures());
		dataSection.set("seed", world.getSeed());
		dataSection.set("world_type", world.getWorldType().toString());
		dataSection.set("spawn_x", spawnLocation.getX());
		dataSection.set("spawn_y", spawnLocation.getY());
		dataSection.set("spawn_z", spawnLocation.getZ());
		dataSection.set("time", world.getTime());
		ConfigurationSection gamerulesSection = dataSection.createSection("gamerules");
		for(String gamerule : world.getGameRules()){
			gamerulesSection.set(gamerule, world.getGameRuleValue(GameRule.getByName(gamerule)));
		}
		WorldBorder worldBorder = WorldBorderManager.getWorldBorder(this.world.getName());
		if(worldBorder!=null){
			//Bukkit.getLogger().info("[WorldManager] Speichere Weltrand für Welt "+world.getName());
			ConfigurationSection worldBorderSection = dataSection.createSection("world_border");
			worldBorderSection.set("center_x", worldBorder.getCenterX());
			worldBorderSection.set("center_z", worldBorder.getCenterZ());
			worldBorderSection.set("radius", worldBorder.getRadius());
			worldBorderSection.set("wrap", worldBorder.doWrap());
			worldBorderSection.set("margin", worldBorder.getMargin());
		}
		else{
			dataSection.remove("world_border");
		}
		yamlConfiguration.save(new File(world.getWorldFolder(),"settings.yml"));
	}
}
