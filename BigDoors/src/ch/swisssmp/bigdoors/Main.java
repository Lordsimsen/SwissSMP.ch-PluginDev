package ch.swisssmp.bigdoors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import ch.swisssmp.bigdoors.PlayerCommand;

public class Main extends JavaPlugin{
	public static Logger logger;
	public static Server server;
	public static YamlConfiguration config;
	public static YamlConfiguration doors;
	public static File configFile;
	public static File doorsFile;
	public static PluginDescriptionFile pdfFile;
	public static File dataFolder;
	public static EventManager eventManager;
	public static Main plugin;
	public static WorldEditPlugin worldedit;
	
	@Override
	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();
		logger = Logger.getLogger("Minecraft");
		logger.info(pdfFile.getName() + " has been enabled (Version: " + pdfFile.getVersion() + ")");
		server = getServer();
		
		Plugin we = server.getPluginManager().getPlugin("WorldEdit");
		try{
			worldedit = (WorldEditPlugin) we;
		}
		catch(Exception e){
			e.printStackTrace();
			logger.info("WorldEdit required. BigDoors won't run properly otherwise.");
			return;
		}
		
		eventManager = new EventManager();
		server.getPluginManager().registerEvents(eventManager, this);
		PlayerCommand playerCommand = new PlayerCommand();
		this.getCommand("BigDoors").setExecutor(playerCommand);
		this.getCommand("BigDoors").setTabCompleter(playerCommand);
		
		configFile = new File(getDataFolder(), "config.yml");
		doorsFile = new File(getDataFolder(), "doors.yml");
		dataFolder = getDataFolder();
		try {
	        firstRun();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		config = new YamlConfiguration();
		doors = new YamlConfiguration();
		loadYamls();
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger.info(pdfFile.getName() + " has been disabled (Version: " + pdfFile.getVersion() + ")");
	}
    private void firstRun() throws Exception {
        if(!configFile.exists()){
        	configFile.getParentFile().mkdirs();
            copy(getResource("config.yml"), configFile);
        }
        if(!doorsFile.exists()){
        	doorsFile.getParentFile().mkdirs();
            copy(getResource("doors.yml"), doorsFile);
        }
    }
    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void loadYamls() {
        try {
        	config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
        	doors.load(doorsFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void saveYamls() {
        try {
        	doors.save(doorsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
        	config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
