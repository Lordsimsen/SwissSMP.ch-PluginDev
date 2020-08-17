package ch.swisssmp.world;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
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

public class WorldManager {

	private static WorldBorderPluginHandler worldBorderPluginHandler;
	private static final Collection<WorldData> worldsData = new ArrayList<WorldData>();

	protected static void initialize(){
		if(Bukkit.getPluginManager().getPlugin("WorldBorder")!=null) worldBorderPluginHandler = WorldBorderPluginHandler.create();
	}

	public static boolean trim(Player player, World world, ch.swisssmp.world.border.WorldBorder borderData, List<String> params) {
		if(worldBorderPluginHandler==null) return false;
		worldBorderPluginHandler.trim(player, world, borderData, params);
		return true;
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
				e.printStackTrace();
				//Skip this World Directory (there is no settings.yml inside to tell WorldManager how to load the World)
			}
		}
	}

	/**
	 * Loads a Minecraft World
	 * @param worldName - The name of the World
	 * @return <code>World</code> if successfully loaded;
	 *         <code>null</code> otherwise
	 */
	public static World loadWorld(String worldName){
		Bukkit.getLogger().info("[WorldManager] Lade Welt "+worldName);
		//Load World Settings
		YamlConfiguration yamlConfiguration = WorldManager.getWorldSettings(worldName);
		if(yamlConfiguration==null || !yamlConfiguration.contains("world")){
			Bukkit.getLogger().warning("[WorldManager] Kann Welt "+worldName+" nicht laden, fehlende oder ungültige Konfigurationsdatei settings.yml im Welt-Ordner.");
			return null;
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
		World world = Bukkit.getWorld(worldName);
		if(world==null) return true;
		for(Chunk chunk : world.getLoadedChunks()){
			chunk.unload(false);
		}
		if(Bukkit.unloadWorld(world, false)){
			File sessionLockFile = new File(world.getWorldFolder(), "session.lock");
			if(sessionLockFile.exists()) sessionLockFile.delete();
			DataSource.getResponse(WorldManagerPlugin.getInstance(), "unload.php", new String[]{
					"world="+URLEncoder.encode(worldName)
			});
			return true;
		}
		else return false;
	}

	/**
	 * Renames a Minecraft World within the main world container
	 * @param worldName - The old name of the World
	 * @param newName - The new name of the World
	 * @return <code>true</code> if successful; otherwise <code>false</code>
	 */
	public static boolean renameWorld(String worldName, String newName){
		File file = new File(Bukkit.getWorldContainer(), worldName);
		File newFile = new File(Bukkit.getWorldContainer(), newName);
		File levelFile = new File(Bukkit.getWorldContainer(), worldName+"/level.dat");
		if(!levelFile.exists() || !levelFile.isFile()){
			Bukkit.getLogger().info(WorldManagerPlugin.getPrefix()+" "+file.getPath()+" ist kein Weltordner.");
			return false;
		}

		return file.renameTo(newFile) && WorldDataPatcher.changeLevelName(newFile, newName);
	}
	
	protected static YamlConfiguration getWorldSettings(String worldName){
		for(WorldData data : worldsData){
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
		worldsData.add(worldData);
		File settingsFile = WorldManager.getSettingsFile(world.getName());
		if(!settingsFile.exists()) worldData.saveSettings();
		return worldData;
	}
	
	protected static void unloadWorldSettings(World world){
		WorldData worldData = WorldManager.getWorldData(world);
		worldsData.remove(worldData);
	}
	
	protected static WorldData getWorldData(World world){
		for(WorldData data : worldsData){
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
			loaded.setAutoSave(false);
			loaded.setKeepSpawnInMemory(false);
			for(Chunk chunk : loaded.getLoadedChunks()){
				chunk.unload(false);
			}
			if(!Bukkit.unloadWorld(loaded, false)){
				Bukkit.getLogger().info("[WorldManager] Konnte Welt "+worldName+" nicht deaktivieren und löschen.");
				return;
			}
			Bukkit.getLogger().info("[WorldManager] Welt "+worldName+" deaktiviert.");
			Bukkit.getScheduler().runTaskLater(WorldManagerPlugin.getInstance(), ()->{
				WorldManager.deleteWorld(worldName);
			}, 1L);
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(WorldManagerPlugin.getInstance(), ()->{
			File worldDirectory = new File(Bukkit.getWorldContainer(),worldName);
			FileUtil.deleteRecursive(worldDirectory);
			Bukkit.getLogger().info("[WorldManager] Lösche Ordner "+worldDirectory.getPath());
			//continue deleting files until this stuff is gone
			if(worldDirectory.exists()){
				Bukkit.getLogger().warning("[WorldManager] Ordner "+worldDirectory.getPath()+" konnte nicht vollständig gelöscht werden.");
			}
			Bukkit.getPluginManager().callEvent(new WorldDeleteEvent(worldName));
			Bukkit.getLogger().info("[WorldManager] Welt "+worldName+" gelöscht.");
		});
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
}
