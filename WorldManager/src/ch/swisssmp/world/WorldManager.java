package ch.swisssmp.world;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ch.swisssmp.utils.ConfigurationSection;
import ch.swisssmp.utils.FileUtil;
import ch.swisssmp.utils.URLEncoder;
import ch.swisssmp.utils.YamlConfiguration;
import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.world.border.WorldBorderCommand;
import ch.swisssmp.world.border.WorldBorderManager;

public class WorldManager extends JavaPlugin{
	protected static Logger logger;
	protected static PluginDescriptionFile pdfFile;
	protected static WorldManager plugin;
	private WorldBorderPluginHandler worldBorderPluginHandler;
	
	private Collection<WorldData> worldsData = new ArrayList<WorldData>();
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		
		Bukkit.getPluginCommand("world").setExecutor(new WorldCommand());
		Bukkit.getPluginCommand("worlds").setExecutor(new WorldsCommand());
		Bukkit.getPluginCommand("worldborder").setExecutor(new WorldBorderCommand());
		
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		if(Bukkit.getPluginManager().getPlugin("WorldGuard")!=null) Bukkit.getPluginManager().registerEvents(new WorldGuardPluginHandler(), this);
		if(Bukkit.getPluginManager().getPlugin("WorldBorder")!=null) worldBorderPluginHandler = WorldBorderPluginHandler.create();
		
		WorldManager.loadWorlds();
		WorldBorderManager.startBorderChecker();
		
		if(Bukkit.getPluginManager().getPlugin("ResourcepackManager")!=null){
			Bukkit.getPluginManager().registerEvents(new ResourcepackListener(), this);
		}
		
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
	}

	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
		HandlerList.unregisterAll(this);
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
	
	public static boolean trim(Player player, World world, ch.swisssmp.world.border.WorldBorder borderData, List<String> params) {
		if(plugin.worldBorderPluginHandler==null) return false;
		plugin.worldBorderPluginHandler.trim(player, world, borderData, params);
		return true;
	}
	
	public static String getPrefix() {
		return ChatColor.RESET+"["+ChatColor.GRAY+plugin.getName()+ChatColor.RESET+"]";
	}
	
	protected static void loadWorlds(){
		File worldContainer = Bukkit.getWorldContainer();
		File levelFile;
		for(File file : worldContainer.listFiles()){
			if(!file.isDirectory()) continue;
			levelFile = new File(file.getPath(), "level.dat");
			if(!levelFile.exists()) continue;
			try{
				WorldManager.loadWorld(file.getName());
			}
			catch(Exception e){
				//Skip this World Directory (there is no settings.yml inside to tell WorldManager how to load the World)
			}
		}
	}

	/**
	 * Loads a Minecraft World
	 * @param worldName - The name of the World
	 * @return <code>World</code> if successfully loaded;
	 *         <code>null</code> otherwise
	 * @throws NullPointerException If settings.yml within World Directory is missing
	 */
	public static World loadWorld(String worldName) throws NullPointerException{
		Bukkit.getLogger().info("[WorldManager] Lade Welt "+worldName);
		//Load World Settings
		YamlConfiguration yamlConfiguration = WorldManager.getWorldSettings(worldName);
		if(yamlConfiguration==null || !yamlConfiguration.contains("world")){
			throw new NullPointerException("[WorldManager] Kann Welt "+worldName+" nicht laden, fehlende oder ungültige Konfigurationsdatei settings.yml im Welt-Ordner.");
		}
		if(yamlConfiguration.contains("load") && !yamlConfiguration.getBoolean("load")){
			Bukkit.unloadWorld(worldName, true);
			return null;
		}
		return WorldLoader.load(worldName, yamlConfiguration.getConfigurationSection("world"));
	}
	
	/**
	 * Unloads a Minecraft World
	 * @param worldName - The name of the World
	 * @return <code>true</code> if the World was unloaded;
	 *         <code>false</code> otherwise
	 */
	public static boolean unloadWorld(String worldName){
		Bukkit.getLogger().info("[WorldManager] Deaktiviere Welt "+worldName);
		if(Bukkit.unloadWorld(worldName, false)){
			DataSource.getResponse(plugin, "unload.php", new String[]{
					"world="+URLEncoder.encode(worldName)
			});
			return true;
		}
		else return false;
	}
	
	protected static YamlConfiguration getWorldSettings(String worldName){
		for(WorldData data : plugin.worldsData){
			if(!data.getWorld().getName().equals(worldName)) continue;
			return data.getSettings();
		}
		
		File settingsFile = WorldManager.getSettingsFile(worldName);
		if(!settingsFile.exists())return null;
		
		return YamlConfiguration.loadConfiguration(settingsFile);
	}
	
	protected static WorldData loadWorldSettings(World world){
		WorldData worldData = new WorldData(world);
		worldData.loadSettings();
		plugin.worldsData.add(worldData);
		File settingsFile = WorldManager.getSettingsFile(world.getName());
		if(!settingsFile.exists()) worldData.saveSettings();
		return worldData;
	}
	
	protected static void unloadWorldSettings(World world){
		WorldData worldData = WorldManager.getWorldData(world);
		plugin.worldsData.remove(worldData);
	}
	
	protected static WorldData getWorldData(World world){
		for(WorldData data : plugin.worldsData){
			if(data.getWorld()==world) return data;
		}
		return WorldManager.loadWorldSettings(world);
	}
	
	/**
	 * Creates a settings.yml in the World Directory (This is done for convenience's sake when the World Folder is transferred between servers)
	 * @param world - The World to save the settings for
	 */
	public static void saveWorldSettings(World world){
		Bukkit.getLogger().info("[WorldManager] Save settings for "+world.getName());
		WorldData worldData = WorldManager.getWorldData(world);
		worldData.saveSettings();
	}
	
	/**
	 * Delete a World Directory and all of its associated Plugin Files (e.g. WorldGuard region data)
	 * @param worldName - The name of the World to be deleted
	 */
	public static void deleteWorld(String worldName){
		World loaded = Bukkit.getWorld(worldName);
		if(loaded!=null){
			
			if(!Bukkit.unloadWorld(loaded, true)){
				Bukkit.getLogger().info("[WorldManager] Konnte Welt "+worldName+" nicht deaktivieren und löschen.");
				return;
			}
			Bukkit.getLogger().info("[WorldManager] Welt "+worldName+" deaktiviert.");
			Bukkit.getScheduler().runTaskLater(WorldManager.plugin, ()->{
				WorldManager.deleteWorld(worldName);
			}, 100L);
			return;
		}
		File worldDirectory = new File(Bukkit.getWorldContainer(),worldName);
		FileUtil.deleteRecursive(worldDirectory);
		Bukkit.getLogger().info("[WorldManager] Lösche Ordner "+worldDirectory.getPath());
		//continue deleting files until this stuff is gone
		if(worldDirectory.exists()){
			Bukkit.getLogger().info("[WorldManager] Ordner "+worldDirectory.getPath()+" konnte nicht vollständig gelöscht werden.");
			Bukkit.getLogger().info("[WorldManager] Löschung der Welt "+worldName+" wird im nächsten Tick fortgesetzt.");
			Bukkit.getScheduler().runTaskLater(WorldManager.plugin, ()->{
				WorldManager.deleteWorld(worldName);
			}, 1l);
			return;
		}
		Bukkit.getPluginManager().callEvent(new WorldDeleteEvent(worldName));
		Bukkit.getLogger().info("[WorldManager] Welt "+worldName+" gelöscht.");
	}
	
	public static String getDisplayName(World world){
		YamlConfiguration yamlConfiguration = WorldManager.getWorldSettings(world.getName());
		if(yamlConfiguration==null) return world.getName();
		ConfigurationSection dataSection = yamlConfiguration.getConfigurationSection("world");
		return dataSection.contains("display_name") ? dataSection.getString("display_name") : world.getName();
	}
	
	public static File getPluginDirectory(Plugin plugin, World world){
		return new File(world.getWorldFolder(),"plugindata/"+plugin.getDataFolder().getName());
	}
	
	protected static File getSettingsFile(String worldName){
		return new File(Bukkit.getWorldContainer(), worldName+"/settings.yml");
	}
	
	public static WorldManager getInstance(){
		return plugin;
	}
}
