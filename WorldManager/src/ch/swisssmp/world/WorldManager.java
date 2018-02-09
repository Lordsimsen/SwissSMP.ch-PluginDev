package ch.swisssmp.world;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;

public class WorldManager extends JavaPlugin{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static WorldManager plugin;
	protected WorldBorderChecker worldBorder;
	private HashMap<String,WorldBorder> worldBorders = new HashMap<String,WorldBorder>();
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		
		Bukkit.getPluginCommand("worldmanager").setExecutor(new PlayerCommand());
		this.loadWorlds();
		this.worldBorder = new WorldBorderChecker();
		this.worldBorder.runTaskTimer(this, 0, 100L);
	}
	
	protected void loadWorlds(){
		Bukkit.getLogger().info("[WorldManager] Lade Welten");
		YamlConfiguration yamlConfiguration = DataSource.getYamlResponse("world/worlds.php");
		if(yamlConfiguration==null) return;
		if(!yamlConfiguration.contains("worlds")) return;
		ConfigurationSection worldsSection = yamlConfiguration.getConfigurationSection("worlds");
		for(String worldName : worldsSection.getKeys(false)){
			String action = worldsSection.getString(worldName);
			if(action.equals("load")){
				if(this.loadWorld(worldName)==null){
					Bukkit.getLogger().info("[WorldManager] Konnte Welt "+worldName+" nicht laden.");
				}
			}
			else if(action.equals("unload")){
				if(!this.unloadWorld(worldName, true)){
					Bukkit.getLogger().info("[WorldManager] Konnte Welt "+worldName+" nicht deaktivieren.");
				}
			}
		}
	}
	
	protected World loadWorld(String worldName){
		Bukkit.getLogger().info("[WorldManager] Lade Welt "+worldName);
		YamlConfiguration yamlConfiguration;
		try {
			yamlConfiguration = DataSource.getYamlResponse("world/load.php", new String[]{
					"world="+URLEncoder.encode(worldName, "utf-8")
			});
			if(yamlConfiguration==null || !yamlConfiguration.contains("world")) return null;
			ConfigurationSection worldSection = yamlConfiguration.getConfigurationSection("world");
			WorldBorder worldBorder = null;
			if(worldSection.contains("world_border")){
				if(this.worldBorders.containsKey(worldName)) this.worldBorders.remove(worldName);
				worldBorder = WorldBorder.create(worldSection.getConfigurationSection("world_border"));
				this.worldBorders.put(worldName, worldBorder);
			}
			World existing = Bukkit.getWorld(worldName);
			if(existing!=null){
				if(worldBorder!=null && !worldBorder.doWrap()){
					existing.getWorldBorder().setCenter(worldBorder.getCenterX(), worldBorder.getCenterZ());
					existing.getWorldBorder().setSize(worldBorder.getRadius()*2);
					existing.getWorldBorder().setWarningDistance(worldBorder.getMargin());
				}
				else if(worldBorder!=null && worldBorder.doWrap()){
					existing.getWorldBorder().reset();
				}
				return existing;
			}
			File mainWorldAdvancementsFile = new File(Bukkit.getWorldContainer(), Bukkit.getWorlds().get(0).getName()+"/data/advancements");
			File worldAdvancementsFile = new File(Bukkit.getWorldContainer(), worldName+"/data/advancements");
			if(worldAdvancementsFile.exists()){
				WorldFileUtil.deleteRecursive(worldAdvancementsFile);
			}
			WorldFileUtil.copyDirectory(mainWorldAdvancementsFile, worldAdvancementsFile);
			WorldCreator creator = new WorldCreator(worldName);
			creator.environment(Environment.valueOf(worldSection.getString("environment")));
			creator.generateStructures(worldSection.getBoolean("generate_structures"));
			creator.seed(worldSection.getLong("seed"));
			creator.type(WorldType.valueOf(worldSection.getString("world_type")));
			World result = Bukkit.createWorld(creator);
			if(result==null) return null;
			if(yamlConfiguration.contains("gamerules")){
				ConfigurationSection gamerulesSection = yamlConfiguration.getConfigurationSection("gamerules");
				for(String gamerule : gamerulesSection.getKeys(false)){
					if(!result.setGameRuleValue(gamerule, gamerulesSection.getString(gamerule))){
						Bukkit.getLogger().info("[WorldManager] Gamerule "+gamerule+" für Welt "+worldName+" konnte nicht auf "+gamerulesSection.getString(gamerule)+" gesetzt werden.");
					}
				}
			}
			result.setSpawnLocation(yamlConfiguration.getInt("spawn_x"), yamlConfiguration.getInt("spawn_y"), yamlConfiguration.getInt("spawn_z"));
			if(worldBorder!=null && !worldBorder.doWrap()){
				result.getWorldBorder().setCenter(worldBorder.getCenterX(), worldBorder.getCenterZ());
				result.getWorldBorder().setSize(worldBorder.getRadius());
				result.getWorldBorder().setWarningDistance(worldBorder.getMargin());
			}
			else if(worldBorder!=null && worldBorder.doWrap()){
				result.getWorldBorder().reset();
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected boolean unloadWorld(String worldName, boolean save){
		Bukkit.getLogger().info("[WorldManager] Deaktiviere Welt "+worldName);
		if(Bukkit.unloadWorld(worldName, save)){
			try {
				DataSource.getResponse("world/unload.php", new String[]{
						"world="+URLEncoder.encode(worldName, "utf-8")
				});
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		else return false;
	}
	
	public WorldBorder getWorldBorder(String worldName){
		return this.worldBorders.get(worldName);
	}

	@Override
	public void onDisable() {
		this.worldBorder.cancel();
		HandlerList.unregisterAll(this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
}
